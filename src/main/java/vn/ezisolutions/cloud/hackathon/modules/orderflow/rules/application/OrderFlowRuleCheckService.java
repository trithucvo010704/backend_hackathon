package vn.ezisolutions.cloud.hackathon.modules.orderflow.rules.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.ezisolutions.cloud.hackathon.modules.orderflow.common.domain.*;
import vn.ezisolutions.cloud.hackathon.modules.orderflow.common.infrastructure.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class OrderFlowRuleCheckService {
    private final DraftOrderRepository draftOrderRepository;
    private final DraftOrderLineRepository draftOrderLineRepository;
    private final CustomerRepository customerRepository;
    private final CustomerCreditProfileRepository creditProfileRepository;
    private final PriceListRepository priceListRepository;
    private final SkuPriceRepository skuPriceRepository;
    private final InventoryBalanceRepository inventoryBalanceRepository;
    private final PriceCheckRepository priceCheckRepository;
    private final InventoryCheckRepository inventoryCheckRepository;
    private final CreditCheckRepository creditCheckRepository;
    private final OrderHoldRepository orderHoldRepository;

    @Transactional
    public void runChecks(DraftOrderEntity order) {
        List<DraftOrderLineEntity> lines = draftOrderLineRepository.findByDraftOrderIdOrderByLineNo(order.getId());
        BigDecimal total = BigDecimal.ZERO;
        for (DraftOrderLineEntity line : lines) {
            if (line.getSelectedSkuId() == null || line.getStatus() == DraftOrderLineStatus.REJECTED) {
                continue;
            }
            total = total.add(runPriceCheck(order, line));
            runInventoryCheck(order, line);
        }
        order.setTotalAmount(total);
        draftOrderRepository.save(order);
        runCreditCheck(order);
        boolean hasOpenHold = orderHoldRepository.existsByDraftOrderIdAndStatus(order.getId(), HoldStatus.OPEN);
        order.setStatus(hasOpenHold ? DraftOrderStatus.ON_HOLD : DraftOrderStatus.READY_FOR_REVIEW);
        if (!hasOpenHold) {
            order.setReadyForReviewAt(OffsetDateTime.now());
        }
        draftOrderRepository.save(order);
    }

    private BigDecimal runPriceCheck(DraftOrderEntity order, DraftOrderLineEntity line) {
        List<SkuPriceEntity> prices = skuPriceRepository.findByOrganizationIdAndSkuIdAndActiveTrueOrderByMinQuantityDesc(
                order.getOrganizationId(), line.getSelectedSkuId());
        SkuPriceEntity price = prices.stream()
                .filter(item -> item.getMinQuantity().compareTo(line.getQuantity()) <= 0)
                .findFirst()
                .orElse(null);
        PriceCheckEntity check = new PriceCheckEntity();
        check.setOrganizationId(order.getOrganizationId());
        check.setDraftOrderLineId(line.getId());
        check.setSkuId(line.getSelectedSkuId());
        check.setQuantity(line.getQuantity());
        check.setCheckedAt(OffsetDateTime.now());
        if (price == null) {
            check.setStatus(RuleCheckStatus.FAIL);
            check.setReason("Không tìm thấy giá áp dụng cho SKU");
            priceCheckRepository.save(check);
            createHold(order, line, HoldType.PRICE_HOLD, "PRICE_MISSING", check.getReason(), Map.of("skuId", line.getSelectedSkuId()));
            return BigDecimal.ZERO;
        }
        check.setPriceListId(price.getPriceListId());
        check.setProposedUnitPrice(price.getUnitPrice());
        check.setReferenceUnitPrice(price.getUnitPrice());
        check.setApprovalFloorPrice(price.getApprovalFloorPrice());
        check.setStatus(RuleCheckStatus.PASS);
        check.setReason("Giá hợp lệ");
        priceCheckRepository.save(check);
        BigDecimal lineAmount = price.getUnitPrice().multiply(line.getQuantity());
        line.setUnitPrice(price.getUnitPrice());
        line.setLineAmount(lineAmount);
        line.setPriceSource("PRICE_LIST:" + price.getPriceListId());
        draftOrderLineRepository.save(line);
        return lineAmount;
    }

    private void runInventoryCheck(DraftOrderEntity order, DraftOrderLineEntity line) {
        InventoryCheckEntity check = new InventoryCheckEntity();
        check.setOrganizationId(order.getOrganizationId());
        check.setDraftOrderLineId(line.getId());
        check.setWarehouseId(order.getWarehouseId());
        check.setSkuId(line.getSelectedSkuId());
        check.setRequestedQuantity(line.getQuantity());
        check.setCheckedAt(OffsetDateTime.now());
        InventoryBalanceEntity balance = inventoryBalanceRepository
                .findByOrganizationIdAndWarehouseIdAndSkuId(order.getOrganizationId(), order.getWarehouseId(), line.getSelectedSkuId())
                .orElse(null);
        if (balance == null) {
            check.setStatus(RuleCheckStatus.FAIL);
            check.setReason("Không có tồn kho cho SKU");
            inventoryCheckRepository.save(check);
            createHold(order, line, HoldType.STOCK_HOLD, "STOCK_MISSING", check.getReason(), Map.of("skuId", line.getSelectedSkuId()));
            return;
        }
        BigDecimal available = balance.getAvailableQuantity() == null
                ? balance.getOnHandQuantity().subtract(balance.getReservedQuantity())
                : balance.getAvailableQuantity();
        check.setOnHandQuantity(balance.getOnHandQuantity());
        check.setReservedQuantity(balance.getReservedQuantity());
        check.setAvailableQuantity(available);
        if (available.compareTo(line.getQuantity()) < 0) {
            check.setStatus(RuleCheckStatus.FAIL);
            check.setReason("Tồn khả dụng không đủ");
            inventoryCheckRepository.save(check);
            createHold(order, line, HoldType.STOCK_HOLD, "STOCK_NOT_ENOUGH", check.getReason(),
                    Map.of("requested", line.getQuantity(), "available", available));
            return;
        }
        check.setStatus(RuleCheckStatus.PASS);
        check.setReason("Tồn kho hợp lệ");
        inventoryCheckRepository.save(check);
    }

    private void runCreditCheck(DraftOrderEntity order) {
        if (order.getCustomerId() == null) {
            return;
        }
        CustomerEntity customer = customerRepository.findById(order.getCustomerId()).orElse(null);
        CustomerCreditProfileEntity profile = creditProfileRepository.findById(order.getCustomerId()).orElse(null);
        if (customer == null || profile == null) {
            return;
        }
        BigDecimal projected = profile.getCurrentDebt()
                .add(profile.getOverdueDebt())
                .add(profile.getPendingApprovedOrderAmount())
                .add(order.getTotalAmount());
        CreditCheckEntity check = new CreditCheckEntity();
        check.setOrganizationId(order.getOrganizationId());
        check.setDraftOrderId(order.getId());
        check.setCustomerId(order.getCustomerId());
        check.setOrderAmount(order.getTotalAmount());
        check.setCreditLimit(profile.getCreditLimit());
        check.setCurrentDebt(profile.getCurrentDebt());
        check.setOverdueDebt(profile.getOverdueDebt());
        check.setPendingApprovedOrderAmount(profile.getPendingApprovedOrderAmount());
        check.setProjectedDebt(projected);
        check.setCheckedAt(OffsetDateTime.now());
        if (profile.getOverdueDebt().compareTo(BigDecimal.ZERO) > 0 || projected.compareTo(profile.getCreditLimit()) > 0) {
            check.setStatus(RuleCheckStatus.FAIL);
            check.setReason("Khách hàng vượt hạn mức hoặc có nợ quá hạn");
            creditCheckRepository.save(check);
            createHold(order, null, HoldType.CREDIT_HOLD, "CREDIT_LIMIT", check.getReason(),
                    Map.of("customer", customer.getName(), "projectedDebt", projected, "creditLimit", profile.getCreditLimit()));
            return;
        }
        check.setStatus(RuleCheckStatus.PASS);
        check.setReason("Công nợ hợp lệ");
        creditCheckRepository.save(check);
    }

    public void createHold(DraftOrderEntity order, DraftOrderLineEntity line, HoldType type,
                           String ruleCode, String reason, Map<String, Object> payload) {
        boolean duplicateOpenHold = orderHoldRepository.findByDraftOrderIdAndStatus(order.getId(), HoldStatus.OPEN).stream()
                .anyMatch(existing -> existing.getHoldType() == type
                        && Objects.equals(existing.getDraftOrderLineId(), line == null ? null : line.getId())
                        && Objects.equals(existing.getRuleCode(), ruleCode));
        if (duplicateOpenHold) {
            return;
        }
        OrderHoldEntity hold = new OrderHoldEntity();
        hold.setOrganizationId(order.getOrganizationId());
        hold.setDraftOrderId(order.getId());
        hold.setDraftOrderLineId(line == null ? null : line.getId());
        hold.setHoldType(type);
        hold.setStatus(HoldStatus.OPEN);
        hold.setSeverity(HoldSeverity.BLOCKING);
        hold.setRuleCode(ruleCode);
        hold.setReason(reason);
        hold.setPayload(new LinkedHashMap<>(payload == null ? Map.of() : payload));
        hold.setCreatedByActorType(type == HoldType.CLARIFICATION_HOLD ? ActorType.AI : ActorType.RULE_ENGINE);
        orderHoldRepository.save(hold);
        if (line != null && type != HoldType.CLARIFICATION_HOLD) {
            line.setStatus(DraftOrderLineStatus.ON_HOLD);
            draftOrderLineRepository.save(line);
        }
    }
}
