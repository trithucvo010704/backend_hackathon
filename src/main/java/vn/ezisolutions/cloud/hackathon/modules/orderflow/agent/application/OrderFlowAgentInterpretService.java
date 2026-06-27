package vn.ezisolutions.cloud.hackathon.modules.orderflow.agent.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.ezisolutions.cloud.hackathon.core.exceptions.CustomValidationException;
import vn.ezisolutions.cloud.hackathon.modules.orderflow.common.application.VietnameseTextNormalizer;
import vn.ezisolutions.cloud.hackathon.modules.orderflow.common.domain.DraftOrderEntity;
import vn.ezisolutions.cloud.hackathon.modules.orderflow.common.domain.DraftOrderLineEntity;
import vn.ezisolutions.cloud.hackathon.modules.orderflow.common.domain.DraftOrderStatus;
import vn.ezisolutions.cloud.hackathon.modules.orderflow.common.domain.HoldStatus;
import vn.ezisolutions.cloud.hackathon.modules.orderflow.common.domain.OrderHoldEntity;
import vn.ezisolutions.cloud.hackathon.modules.orderflow.common.infrastructure.DraftOrderLineRepository;
import vn.ezisolutions.cloud.hackathon.modules.orderflow.common.infrastructure.DraftOrderRepository;
import vn.ezisolutions.cloud.hackathon.modules.orderflow.common.infrastructure.OrderHoldRepository;
import vn.ezisolutions.cloud.hackathon.modules.orderflow.common.security.OrderFlowPrincipal;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderFlowAgentInterpretService {
    private static final List<String> MVP_GUARDRAILS = List.of(
            "AI suggestions are advisory.",
            "AI cannot approve orders, release holds, export documents, or override rule checks.",
            "Human review and backend rule checks remain the source of truth."
    );

    private final VietnameseTextNormalizer normalizer;
    private final DraftOrderRepository draftOrderRepository;
    private final DraftOrderLineRepository draftOrderLineRepository;
    private final OrderHoldRepository orderHoldRepository;

    @Transactional(readOnly = true)
    public AgentInterpretResponse interpret(OrderFlowPrincipal user, AgentInterpretRequest request) {
        String message = request == null ? "" : text(request.message());
        if (message.isBlank()) {
            throw new CustomValidationException("Message is required", null);
        }
        if (request.orderId() == null) {
            return interpretWithoutOrder(message);
        }
        DraftOrderEntity order = draftOrderRepository.findById(request.orderId())
                .orElseThrow(() -> new CustomValidationException("Draft order not found", null));
        if (!order.getOrganizationId().equals(user.organizationId())) {
            throw new CustomValidationException("Draft order is outside current organization", null);
        }
        List<DraftOrderLineEntity> lines = draftOrderLineRepository.findByDraftOrderIdOrderByLineNo(order.getId());
        List<OrderHoldEntity> openHolds = orderHoldRepository.findByDraftOrderIdAndStatus(order.getId(), HoldStatus.OPEN);

        String intent = intent(message);
        Map<String, Object> facts = new LinkedHashMap<>();
        facts.put("orderNo", order.getOrderNo());
        facts.put("status", order.getStatus());
        facts.put("lineCount", lines.size());
        facts.put("openHoldCount", openHolds.size());
        facts.put("unmatchedLineCount", lines.stream().filter(line -> line.getSelectedSkuId() == null).count());
        facts.put("totalAmount", order.getTotalAmount());

        List<String> actions = suggestedActions(order, lines, openHolds, intent);
        String reply = reply(order, lines, openHolds, intent, message, actions);
        boolean canExport = openHolds.isEmpty() && order.getStatus() == DraftOrderStatus.APPROVED;

        return new AgentInterpretResponse(reply, intent, order.getId(), MVP_GUARDRAILS, actions, canExport, openHolds.size(), facts);
    }

    private AgentInterpretResponse interpretWithoutOrder(String message) {
        String intent = intent(message);
        List<String> actions = switch (intent) {
            case "BLOCKED_AUTOMATION" -> List.of("Open the draft order review screen", "Ask a Sale Admin to approve/release/export manually");
            case "CREATE_ORDER_HELP" -> List.of("Choose customer and warehouse", "Paste raw order text", "Create draft order from text");
            default -> List.of("Open a draft order", "Ask about holds, SKU match, stock, credit, or next review step");
        };
        String reply = switch (intent) {
            case "BLOCKED_AUTOMATION" -> "I cannot perform approval, hold release, or export from chat. I can explain what a reviewer should check next.";
            case "CREATE_ORDER_HELP" -> "To create an MVP draft order, select a customer and warehouse, paste the customer's raw text, then run AI extraction and backend checks.";
            default -> "Send me a draft order context and I can explain open holds, SKU matches, stock, credit, and the next safe review step.";
        };
        Map<String, Object> facts = Map.of("hasOrderContext", false);
        return new AgentInterpretResponse(reply, intent, null, MVP_GUARDRAILS, actions, false, 0, facts);
    }

    private String reply(DraftOrderEntity order, List<DraftOrderLineEntity> lines, List<OrderHoldEntity> openHolds,
                         String intent, String message, List<String> actions) {
        if ("BLOCKED_AUTOMATION".equals(intent)) {
            return "I cannot approve, release holds, or export documents from chat. For " + order.getOrderNo()
                    + ", a human reviewer must use the review actions after backend checks pass.";
        }
        if (!openHolds.isEmpty()) {
            OrderHoldEntity first = openHolds.getFirst();
            return "Order " + order.getOrderNo() + " is blocked by " + openHolds.size()
                    + " open hold(s). Start with " + first.getHoldType() + " / " + first.getRuleCode()
                    + ": " + first.getReason() + ". Suggested next step: " + actions.getFirst() + ".";
        }
        long unmatched = lines.stream().filter(line -> line.getSelectedSkuId() == null).count();
        if (unmatched > 0) {
            return "Order " + order.getOrderNo() + " has " + unmatched
                    + " line(s) without a selected SKU. Review top SKU candidates before running approval.";
        }
        if (order.getStatus() == DraftOrderStatus.APPROVED) {
            return "Order " + order.getOrderNo()
                    + " is approved and has no open holds. A reviewer can generate the quote or pick list.";
        }
        if (order.getStatus() == DraftOrderStatus.READY_FOR_REVIEW) {
            return "Order " + order.getOrderNo()
                    + " has no open holds and all lines are matched. A Sale Admin can do the final review and approve.";
        }
        return "Order " + order.getOrderNo() + " is currently " + order.getStatus()
                + ". I can explain holds, SKU matching, stock, credit, and review checklist items.";
    }

    private List<String> suggestedActions(DraftOrderEntity order, List<DraftOrderLineEntity> lines,
                                          List<OrderHoldEntity> openHolds, String intent) {
        if ("BLOCKED_AUTOMATION".equals(intent)) {
            return List.of("Use the review screen for manual approval/release/export", "Check audit and review logs after action");
        }
        if (!openHolds.isEmpty()) {
            OrderHoldEntity first = openHolds.getFirst();
            return List.of("Resolve " + first.getHoldType() + " on the review screen", "Run checks again after changing SKU or quantity");
        }
        if (lines.stream().anyMatch(line -> line.getSelectedSkuId() == null)) {
            return List.of("Select SKU for unmatched lines", "Run rule checks after SKU selection");
        }
        if (order.getStatus() == DraftOrderStatus.APPROVED) {
            return List.of("Generate quote", "Generate pick list", "Review audit events");
        }
        return List.of("Review matched lines", "Approve if policy allows", "Generate documents after approval");
    }

    private String intent(String message) {
        String normalized = normalizer.normalize(message);
        if (containsAny(normalized, "approve", "duyet", "release", "giai phong", "bo hold", "export", "xuat", "gui khach")) {
            return "BLOCKED_AUTOMATION";
        }
        if (containsAny(normalized, "hold", "blocked", "chan", "loi", "vi sao")) {
            return "EXPLAIN_HOLDS";
        }
        if (containsAny(normalized, "sku", "hang nao", "mat hang", "match", "chon")) {
            return "SKU_REVIEW";
        }
        if (containsAny(normalized, "ton", "kho", "stock", "inventory")) {
            return "INVENTORY_REVIEW";
        }
        if (containsAny(normalized, "cong no", "credit", "han muc", "no qua han")) {
            return "CREDIT_REVIEW";
        }
        if (containsAny(normalized, "tao don", "create order", "paste", "raw text")) {
            return "CREATE_ORDER_HELP";
        }
        return "ORDER_GUIDANCE";
    }

    private boolean containsAny(String value, String... needles) {
        for (String needle : needles) {
            if (value.contains(needle)) {
                return true;
            }
        }
        return false;
    }

    private String text(String value) {
        return value == null ? "" : value.trim();
    }

    public record AgentInterpretRequest(
            UUID orderId,
            String message,
            Map<String, Object> context,
            String organizationCode,
            UUID actorUserId,
            String customerCode,
            String warehouseCode
    ) {
    }

    public record AgentInterpretResponse(
            String reply,
            String intent,
            UUID orderId,
            List<String> guardrails,
            List<String> suggestedActions,
            boolean canExport,
            int openHoldCount,
            Map<String, Object> facts
    ) {
    }
}
