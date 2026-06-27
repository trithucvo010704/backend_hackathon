package vn.ezisolutions.cloud.hackathon.modules.orderflow.ai.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.ezisolutions.cloud.hackathon.core.common.BaseResponse;
import vn.ezisolutions.cloud.hackathon.modules.orderflow.ai.domain.OrderExtractionCommand;
import vn.ezisolutions.cloud.hackathon.modules.orderflow.ai.domain.OrderExtractionGateway;
import vn.ezisolutions.cloud.hackathon.modules.orderflow.ai.domain.OrderExtractionResult;
import vn.ezisolutions.cloud.hackathon.modules.orderflow.common.security.OrderFlowSecurity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/agent")
@RequiredArgsConstructor
public class OrderFlowAgentController {
    private static final List<String> DEFAULT_GUARDRAILS = List.of(
            "AI chỉ hỗ trợ Sale Admin review nội bộ, không tự gửi khách.",
            "AI không tự approve đơn, release hold, hứa tồn kho, hứa công nợ hoặc chốt giá khi chưa có rule check.",
            "SKU mơ hồ phải để human review chọn, không auto-select."
    );

    private final OrderExtractionGateway extractionGateway;

    @PostMapping("/interpret")
    public BaseResponse interpret(@Valid @RequestBody AgentInterpretRequest request) {
        var currentUser = OrderFlowSecurity.currentUser();
        OrderExtractionResult extraction = extractionGateway.extract(new OrderExtractionCommand(
                currentUser.organizationId(),
                null,
                null,
                request.draftOrderId(),
                null,
                request.message()
        ));

        return BaseResponse.success(new AgentInterpretResponse(
                buildAnswer(extraction),
                extraction,
                buildSuggestedActions(extraction),
                DEFAULT_GUARDRAILS
        ));
    }

    private String buildAnswer(OrderExtractionResult extraction) {
        int lineCount = extraction.lines() == null ? 0 : extraction.lines().size();
        if (hasText(extraction.clarificationQuestion())) {
            return "AI đã đọc nội dung và cần Sale Admin làm rõ trước khi tiếp tục: "
                    + extraction.clarificationQuestion();
        }
        if (lineCount == 0) {
            return "AI đã đọc nội dung nhưng chưa bóc được dòng hàng rõ ràng. Vui lòng bổ sung tên hàng, quy cách và số lượng.";
        }
        return "AI đã gọi extraction API và bóc được " + lineCount
                + " dòng hàng. Vui lòng kiểm tra SKU candidates, rule giá/tồn/công nợ và các hold trước khi approve.";
    }

    private List<String> buildSuggestedActions(OrderExtractionResult extraction) {
        List<String> actions = new ArrayList<>();
        if (extraction.missingInformation() != null && !extraction.missingInformation().isEmpty()) {
            actions.add("Bổ sung thông tin còn thiếu: " + String.join(", ", extraction.missingInformation()));
        }
        if (hasText(extraction.clarificationQuestion())) {
            actions.add("Gửi câu hỏi clarification cho khách hoặc cập nhật raw text.");
        }
        if (extraction.lines() != null && !extraction.lines().isEmpty()) {
            actions.add("Tạo draft order hoặc quay về Review để chạy SKU matching/rule checks.");
        }
        if (actions.isEmpty()) {
            actions.add("Nhập thêm nội dung đơn hàng để AI phân tích.");
        }
        return actions;
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }

    public record AgentInterpretRequest(
            @JsonProperty("organization_code") String organizationCode,
            @JsonProperty("actor_user_id") UUID actorUserId,
            @JsonProperty("customer_code") String customerCode,
            @JsonProperty("warehouse_code") String warehouseCode,
            @JsonProperty("draft_order_id") UUID draftOrderId,
            @NotBlank String message,
            Map<String, Object> context
    ) {
    }

    public record AgentInterpretResponse(
            String answer,
            OrderExtractionResult extraction,
            List<String> suggestedActions,
            List<String> guardrails
    ) {
    }
}
