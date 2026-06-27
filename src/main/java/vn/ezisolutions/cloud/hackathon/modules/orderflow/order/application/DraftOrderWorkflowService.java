package vn.ezisolutions.cloud.hackathon.modules.orderflow.order.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.ezisolutions.cloud.hackathon.core.exceptions.CustomValidationException;
import vn.ezisolutions.cloud.hackathon.modules.orderflow.ai.domain.OrderExtractionCommand;
import vn.ezisolutions.cloud.hackathon.modules.orderflow.ai.domain.OrderExtractionGateway;
import vn.ezisolutions.cloud.hackathon.modules.orderflow.ai.domain.OrderExtractionResult;
import vn.ezisolutions.cloud.hackathon.modules.orderflow.audit.application.OrderFlowLogService;
import vn.ezisolutions.cloud.hackathon.modules.orderflow.common.application.VietnameseTextNormalizer;
import vn.ezisolutions.cloud.hackathon.modules.orderflow.common.domain.*;
import vn.ezisolutions.cloud.hackathon.modules.orderflow.common.infrastructure.*;
import vn.ezisolutions.cloud.hackathon.modules.orderflow.common.security.OrderFlowPrincipal;
import vn.ezisolutions.cloud.hackathon.modules.orderflow.matching.application.SkuMatchingService;
import vn.ezisolutions.cloud.hackathon.modules.orderflow.rules.application.OrderFlowRuleCheckService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DraftOrderWorkflowService {
    private final VietnameseTextNormalizer normalizer;
    private final OrderExtractionGateway extractionGateway;
    private final SkuMatchingService skuMatchingService;
    private final OrderFlowRuleCheckService ruleCheckService;
    private final OrderFlowLogService logService;
    private final RawOrderTextRepository rawOrderTextRepository;
    private final DraftOrderRepository draftOrderRepository;
    private final DraftOrderLineRepository draftOrderLineRepository;
    private final SkuCandidateRepository skuCandidateRepository;
    private final OrderHoldRepository orderHoldRepository;
    private final PriceCheckRepository priceCheckRepository;
    private final InventoryCheckRepository inventoryCheckRepository;
    private final CreditCheckRepository creditCheckRepository;
    private final InventoryBalanceRepository inventoryBalanceRepository;
    private final InventoryReservationRepository inventoryReservationRepository;
    private final DraftOrderDocumentRepository documentRepository;
    private final ProductSkuRepository productSkuRepository;
    private final ReviewActionRepository reviewActionRepository;
    private final AuditEventRepository auditEventRepository;
    private final ProcessingEventRepository processingEventRepository;

    @Transactional
    public Map<String, Object> createFromText(OrderFlowPrincipal user, CreateDraftOrderCommand command) {
        RawOrderTextEntity raw = new RawOrderTextEntity();
        raw.setOrganizationId(user.organizationId());
        raw.setSourceChannel("MANUAL_TEXT");
        raw.setRawText(command.rawText());
        raw.setNormalizedText(normalizer.normalize(command.rawText()));
        raw.setPastedByUserId(user.userId());
        raw.setReceivedAt(OffsetDateTime.now());
        raw.setCreatedAt(OffsetDateTime.now());
        raw = rawOrderTextRepository.save(raw);

        DraftOrderEntity order = new DraftOrderEntity();
        order.setOrganizationId(user.organizationId());
        order.setOrderNo(nextOrderNo(user.organizationId()));
        order.setRawOrderTextId(raw.getId());
        order.setCustomerId(command.customerId());
        order.setProjectId(command.projectId());
        order.setWarehouseId(command.warehouseId());
        order.setStatus(DraftOrderStatus.EXTRACTING);
        order.setTotalAmount(BigDecimal.ZERO);
        order.setCreatedByUserId(user.userId());
        order = draftOrderRepository.save(order);

        logService.processing(user.organizationId(), order.getId(), "RAW_TEXT_RECEIVED", "SUCCESS", Map.of("rawOrderTextId", raw.getId()));
        logService.processing(user.organizationId(), order.getId(), "AI_EXTRACTION_STARTED", "STARTED", Map.of());
        OrderExtractionResult extraction = extractionGateway.extract(new OrderExtractionCommand(
                user.organizationId(), command.customerId(), command.projectId(), order.getId(), raw.getId(), command.rawText()));
        raw.setExtractionResult(new LinkedHashMap<>(extraction.rawResult()));
        rawOrderTextRepository.save(raw);

        order.setRequestedDeliveryDate(extraction.requestedDeliveryDate());
        order.setDeliveryNote(extraction.deliveryNote());
        order.setClarificationQuestion(extraction.clarificationQuestion());
        draftOrderRepository.save(order);
        logService.processing(user.organizationId(), order.getId(), "AI_EXTRACTION_COMPLETED", "SUCCESS", Map.of("lineCount", extraction.lines().size()));
        logService.audit(user.organizationId(), order.getId(), "DraftOrder", order.getId(), ActorType.AI, null, "AI_EXTRACTION", extraction.rawResult());

        persistLines(order, extraction);
        for (DraftOrderLineEntity line : draftOrderLineRepository.findByDraftOrderIdOrderByLineNo(order.getId())) {
            skuMatchingService.matchLine(line);
            DraftOrderLineEntity refreshed = draftOrderLineRepository.findById(line.getId()).orElseThrow();
            if (refreshed.getStatus() == DraftOrderLineStatus.PENDING_MATCH || refreshed.getStatus() == DraftOrderLineStatus.NEEDS_CLARIFICATION) {
                ruleCheckService.createHold(order, refreshed, HoldType.CLARIFICATION_HOLD, "SKU_AMBIGUOUS",
                        refreshed.getClarificationQuestion() == null ? "Cần Sale Admin chọn SKU phù hợp" : refreshed.getClarificationQuestion(),
                        Map.of("lineId", refreshed.getId()));
            }
        }
        logService.processing(user.organizationId(), order.getId(), "SKU_MATCHING_COMPLETED", "SUCCESS", Map.of());
        ruleCheckService.runChecks(order);
        recomputeStatus(order.getId());
        return detail(order.getId());
    }

    public List<DraftOrderEntity> list(OrderFlowPrincipal user, DraftOrderStatus status) {
        if (status == null) {
            return draftOrderRepository.findByOrganizationIdOrderByCreatedAtDesc(user.organizationId());
        }
        return draftOrderRepository.findByOrganizationIdAndStatusOrderByCreatedAtDesc(user.organizationId(), status);
    }

    public Map<String, Object> detail(UUID orderId) {
        DraftOrderEntity order = draftOrderRepository.findById(orderId).orElseThrow();
        List<DraftOrderLineEntity> lines = draftOrderLineRepository.findByDraftOrderIdOrderByLineNo(orderId);
        Map<UUID, List<SkuCandidateEntity>> candidates = lines.stream()
                .collect(Collectors.toMap(DraftOrderLineEntity::getId, line -> skuCandidateRepository.findByDraftOrderLineIdOrderByRankNo(line.getId())));
        Map<UUID, List<PriceCheckEntity>> priceChecks = lines.stream()
                .collect(Collectors.toMap(DraftOrderLineEntity::getId, line -> priceCheckRepository.findByDraftOrderLineId(line.getId())));
        Map<UUID, List<InventoryCheckEntity>> inventoryChecks = lines.stream()
                .collect(Collectors.toMap(DraftOrderLineEntity::getId, line -> inventoryCheckRepository.findByDraftOrderLineId(line.getId())));
        RawOrderTextEntity raw = order.getRawOrderTextId() == null ? null : rawOrderTextRepository.findById(order.getRawOrderTextId()).orElse(null);
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("order", order);
        response.put("rawOrderText", raw);
        response.put("lines", lines);
        response.put("candidatesByLineId", candidates);
        response.put("priceChecksByLineId", priceChecks);
        response.put("inventoryChecksByLineId", inventoryChecks);
        response.put("creditChecks", creditCheckRepository.findByDraftOrderId(orderId));
        response.put("holds", orderHoldRepository.findByDraftOrderIdOrderByCreatedAtDesc(orderId));
        response.put("documents", documentRepository.findByDraftOrderIdOrderByCreatedAtDesc(orderId));
        response.put("processingEvents", processingEventRepository.findByDraftOrderIdOrderByStartedAtDesc(orderId));
        response.put("auditEvents", auditEventRepository.findByDraftOrderIdOrderByCreatedAtDesc(orderId));
        response.put("reviewActions", reviewActionRepository.findByDraftOrderIdOrderByCreatedAtDesc(orderId));
        return response;
    }

    @Transactional
    public DraftOrderLineEntity selectSku(OrderFlowPrincipal user, UUID lineId, UUID skuId) {
        DraftOrderLineEntity line = draftOrderLineRepository.findById(lineId).orElseThrow();
        line.setSelectedSkuId(skuId);
        line.setSelectedByUserId(user.userId());
        line.setSelectedAt(OffsetDateTime.now());
        line.setStatus(DraftOrderLineStatus.MATCHED);
        draftOrderLineRepository.save(line);
        logService.review(line.getOrganizationId(), line.getDraftOrderId(), line.getId(), "SELECT_SKU", "User selected SKU", user.userId(), Map.of("skuId", skuId));
        logService.audit(line.getOrganizationId(), line.getDraftOrderId(), "DraftOrderLine", line.getId(), ActorType.USER, user.userId(), "SELECT_SKU", Map.of("skuId", skuId));
        return line;
    }

    @Transactional
    public void runChecks(UUID orderId) {
        DraftOrderEntity order = draftOrderRepository.findById(orderId).orElseThrow();
        ruleCheckService.runChecks(order);
        recomputeStatus(orderId);
    }

    @Transactional
    public OrderHoldEntity releaseHold(OrderFlowPrincipal user, UUID holdId, String note) {
        OrderHoldEntity hold = orderHoldRepository.findById(holdId).orElseThrow();
        if (note == null || note.isBlank()) {
            throw new CustomValidationException("Cần nhập ghi chú khi release hold", null);
        }
        hold.setStatus(HoldStatus.RELEASED);
        hold.setReleasedByUserId(user.userId());
        hold.setReleasedAt(OffsetDateTime.now());
        hold.setReleaseNote(note);
        orderHoldRepository.save(hold);
        logService.review(hold.getOrganizationId(), hold.getDraftOrderId(), hold.getDraftOrderLineId(), "RELEASE_HOLD", note, user.userId(), Map.of("holdId", holdId));
        recomputeStatus(hold.getDraftOrderId());
        return hold;
    }

    @Transactional
    public DraftOrderEntity approve(OrderFlowPrincipal user, UUID orderId) {
        DraftOrderEntity order = draftOrderRepository.findById(orderId).orElseThrow();
        if (order.getStatus() == DraftOrderStatus.APPROVED || order.getStatus() == DraftOrderStatus.EXPORTED) {
            return order;
        }
        if (!orderHoldRepository.findByDraftOrderIdAndStatus(orderId, HoldStatus.OPEN).isEmpty()) {
            throw new CustomValidationException("Không thể duyệt đơn khi còn hold mở", null);
        }
        List<DraftOrderLineEntity> lines = draftOrderLineRepository.findByDraftOrderIdOrderByLineNo(orderId);
        for (DraftOrderLineEntity line : lines) {
            if (line.getStatus() != DraftOrderLineStatus.REJECTED && line.getSelectedSkuId() == null) {
                throw new CustomValidationException("Còn dòng hàng chưa chọn SKU", null);
            }
        }
        for (DraftOrderLineEntity line : lines) {
            if (line.getStatus() == DraftOrderLineStatus.REJECTED) {
                continue;
            }
            InventoryReservationEntity reservation = new InventoryReservationEntity();
            reservation.setOrganizationId(order.getOrganizationId());
            reservation.setWarehouseId(order.getWarehouseId());
            reservation.setSkuId(line.getSelectedSkuId());
            reservation.setDraftOrderLineId(line.getId());
            reservation.setQuantity(line.getQuantity());
            reservation.setStatus(ReservationStatus.ACTIVE);
            reservation.setReservedAt(OffsetDateTime.now());
            inventoryReservationRepository.save(reservation);
            inventoryBalanceRepository.findByOrganizationIdAndWarehouseIdAndSkuId(order.getOrganizationId(), order.getWarehouseId(), line.getSelectedSkuId())
                    .ifPresent(balance -> {
                        balance.setReservedQuantity(balance.getReservedQuantity().add(line.getQuantity()));
                        balance.setAvailableQuantity(balance.getOnHandQuantity().subtract(balance.getReservedQuantity()));
                        balance.setUpdatedAt(OffsetDateTime.now());
                        inventoryBalanceRepository.save(balance);
                    });
            line.setStatus(DraftOrderLineStatus.APPROVED);
            draftOrderLineRepository.save(line);
        }
        order.setStatus(DraftOrderStatus.APPROVED);
        order.setApprovedByUserId(user.userId());
        order.setApprovedAt(OffsetDateTime.now());
        draftOrderRepository.save(order);
        logService.review(order.getOrganizationId(), order.getId(), null, "APPROVE_ORDER", "Order approved", user.userId(), Map.of("orderId", orderId));
        logService.audit(order.getOrganizationId(), order.getId(), "DraftOrder", order.getId(), ActorType.USER, user.userId(), "ORDER_APPROVED", Map.of("orderId", orderId));
        return order;
    }

    @Transactional
    public DraftOrderEntity reject(OrderFlowPrincipal user, UUID orderId, String reason) {
        DraftOrderEntity order = draftOrderRepository.findById(orderId).orElseThrow();
        order.setStatus(DraftOrderStatus.REJECTED);
        order.setRejectedByUserId(user.userId());
        order.setRejectedAt(OffsetDateTime.now());
        draftOrderRepository.save(order);
        logService.review(order.getOrganizationId(), orderId, null, "REJECT_ORDER", reason, user.userId(), Map.of("reason", reason == null ? "" : reason));
        logService.audit(order.getOrganizationId(), orderId, "DraftOrder", orderId, ActorType.USER, user.userId(), "ORDER_REJECTED", Map.of("reason", reason == null ? "" : reason));
        return order;
    }

    @Transactional
    public DraftOrderLineEntity updateLine(OrderFlowPrincipal user, UUID lineId, BigDecimal quantity, String unit, String itemDescription) {
        DraftOrderLineEntity line = draftOrderLineRepository.findById(lineId).orElseThrow();
        if (quantity != null) {
            line.setQuantity(quantity);
        }
        if (unit != null) {
            line.setRequestedUnit(unit);
        }
        if (itemDescription != null) {
            line.setItemDescription(itemDescription);
        }
        draftOrderLineRepository.save(line);
        logService.review(line.getOrganizationId(), line.getDraftOrderId(), lineId, "EDIT_LINE", "User edited line", user.userId(),
                Map.of("quantity", line.getQuantity(), "unit", line.getRequestedUnit() == null ? "" : line.getRequestedUnit()));
        return line;
    }

    @Transactional
    public DraftOrderLineEntity rejectLine(OrderFlowPrincipal user, UUID lineId, String reason) {
        DraftOrderLineEntity line = draftOrderLineRepository.findById(lineId).orElseThrow();
        line.setStatus(DraftOrderLineStatus.REJECTED);
        draftOrderLineRepository.save(line);
        logService.review(line.getOrganizationId(), line.getDraftOrderId(), lineId, "REJECT_LINE", reason, user.userId(), Map.of("reason", reason == null ? "" : reason));
        return line;
    }

    @Transactional
    public DraftOrderDocumentEntity generateDocument(OrderFlowPrincipal user, UUID orderId, DocumentType type) {
        DraftOrderEntity order = draftOrderRepository.findById(orderId).orElseThrow();
        if (!orderHoldRepository.findByDraftOrderIdAndStatus(orderId, HoldStatus.OPEN).isEmpty()) {
            throw new CustomValidationException("Không thể tạo tài liệu khi còn hold mở", null);
        }
        List<DraftOrderLineEntity> lines = draftOrderLineRepository.findByDraftOrderIdOrderByLineNo(orderId);
        for (DraftOrderLineEntity line : lines) {
            if (line.getStatus() != DraftOrderLineStatus.REJECTED && line.getSelectedSkuId() == null) {
                throw new CustomValidationException("Không thể tạo tài liệu khi còn dòng hàng chưa chọn SKU", null);
            }
        }
        if (type == DocumentType.PICK_LIST
                && order.getStatus() != DraftOrderStatus.APPROVED
                && order.getStatus() != DraftOrderStatus.EXPORTED) {
            throw new CustomValidationException("Chỉ tạo phiếu lấy hàng sau khi đơn được duyệt", null);
        }
        Map<UUID, ProductSkuEntity> skus = productSkuRepository.findAllById(lines.stream()
                        .map(DraftOrderLineEntity::getSelectedSkuId)
                        .filter(id -> id != null)
                        .toList())
                .stream().collect(Collectors.toMap(ProductSkuEntity::getId, sku -> sku));
        String html = buildHtml(order, lines, skus, type);
        DraftOrderDocumentEntity document = new DraftOrderDocumentEntity();
        document.setOrganizationId(order.getOrganizationId());
        document.setDraftOrderId(orderId);
        document.setDocumentType(type);
        document.setStatus(DocumentStatus.GENERATED);
        document.setHtmlSnapshot(html);
        document.setGeneratedByUserId(user.userId());
        document.setGeneratedAt(OffsetDateTime.now());
        document.setCreatedAt(OffsetDateTime.now());
        documentRepository.save(document);
        if (type == DocumentType.PICK_LIST) {
            order.setStatus(DraftOrderStatus.EXPORTED);
            order.setUpdatedAt(OffsetDateTime.now());
            draftOrderRepository.save(order);
        }
        logService.review(order.getOrganizationId(), orderId, null, "GENERATE_" + type.name(), "Generated document", user.userId(), Map.of("documentId", document.getId()));
        return document;
    }

    private void persistLines(DraftOrderEntity order, OrderExtractionResult extraction) {
        int lineNo = 1;
        for (OrderExtractionResult.ExtractedLine extracted : extraction.lines()) {
            DraftOrderLineEntity line = new DraftOrderLineEntity();
            line.setOrganizationId(order.getOrganizationId());
            line.setDraftOrderId(order.getId());
            line.setLineNo(lineNo++);
            line.setRawLineText(requiredRawLine(extracted.rawLineText()));
            line.setItemDescription(extracted.itemDescription());
            line.setQuantity(extracted.quantity() == null ? BigDecimal.ZERO : extracted.quantity());
            line.setRequestedUnit(extracted.requestedUnit());
            line.setExtractedAttributes(extracted.extractedAttributes() == null ? Map.of() : extracted.extractedAttributes());
            line.setConfidenceScore(extracted.confidenceScore());
            line.setClarificationQuestion(extracted.clarificationQuestion());
            line.setStatus(line.getQuantity().compareTo(BigDecimal.ZERO) <= 0 ? DraftOrderLineStatus.NEEDS_CLARIFICATION : DraftOrderLineStatus.EXTRACTED);
            draftOrderLineRepository.save(line);
            if (line.getStatus() == DraftOrderLineStatus.NEEDS_CLARIFICATION) {
                ruleCheckService.createHold(order, line, HoldType.CLARIFICATION_HOLD, "MISSING_QUANTITY", "Thiếu số lượng hợp lệ", Map.of("lineNo", line.getLineNo()));
            }
        }
        if (lineNo == 1) {
            ruleCheckService.createHold(order, null, HoldType.CLARIFICATION_HOLD, "NO_LINES", "AI chưa bóc được dòng hàng nào", Map.of());
        }
    }

    private String requiredRawLine(String value) {
        return value == null || value.isBlank() ? "UNKNOWN_LINE" : value;
    }

    private void recomputeStatus(UUID orderId) {
        DraftOrderEntity order = draftOrderRepository.findById(orderId).orElseThrow();
        List<OrderHoldEntity> holds = orderHoldRepository.findByDraftOrderIdAndStatus(orderId, HoldStatus.OPEN);
        if (holds.stream().anyMatch(hold -> hold.getHoldType() == HoldType.CLARIFICATION_HOLD)) {
            order.setStatus(DraftOrderStatus.NEEDS_CLARIFICATION);
        } else if (!holds.isEmpty()) {
            order.setStatus(DraftOrderStatus.ON_HOLD);
        } else if (order.getStatus() != DraftOrderStatus.APPROVED && order.getStatus() != DraftOrderStatus.REJECTED) {
            order.setStatus(DraftOrderStatus.READY_FOR_REVIEW);
            order.setReadyForReviewAt(OffsetDateTime.now());
        }
        draftOrderRepository.save(order);
    }

    private String nextOrderNo(UUID organizationId) {
        return "DO-" + LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE) + "-" +
                String.format("%04d", draftOrderRepository.countByOrganizationId(organizationId) + 1);
    }

    private String buildHtml(DraftOrderEntity order, List<DraftOrderLineEntity> lines, Map<UUID, ProductSkuEntity> skus, DocumentType type) {
        String title = type == DocumentType.QUOTE ? "Báo giá" : "Phiếu lấy hàng";
        StringBuilder html = new StringBuilder();
        html.append("<h1>").append(title).append("</h1>");
        html.append("<p>Order: ").append(order.getOrderNo()).append("</p>");
        html.append("<table border=\"1\" cellspacing=\"0\" cellpadding=\"6\"><thead><tr>");
        html.append("<th>SKU</th><th>Tên hàng</th><th>Số lượng</th><th>Đơn giá</th><th>Thành tiền</th>");
        html.append("</tr></thead><tbody>");
        for (DraftOrderLineEntity line : lines) {
            ProductSkuEntity sku = skus.get(line.getSelectedSkuId());
            html.append("<tr><td>").append(sku == null ? "" : sku.getSkuCode()).append("</td>")
                    .append("<td>").append(sku == null ? line.getItemDescription() : sku.getProductName()).append("</td>")
                    .append("<td>").append(line.getQuantity()).append(" ").append(line.getRequestedUnit() == null ? "" : line.getRequestedUnit()).append("</td>")
                    .append("<td>").append(line.getUnitPrice() == null ? "" : line.getUnitPrice()).append("</td>")
                    .append("<td>").append(line.getLineAmount() == null ? "" : line.getLineAmount()).append("</td></tr>");
        }
        html.append("</tbody></table><strong>Total: ").append(order.getTotalAmount()).append("</strong>");
        return html.toString();
    }

    public record CreateDraftOrderCommand(UUID customerId, UUID projectId, UUID warehouseId, String rawText) {
    }
}
