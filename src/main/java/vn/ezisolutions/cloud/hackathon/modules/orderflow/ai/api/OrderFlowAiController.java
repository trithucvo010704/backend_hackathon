package vn.ezisolutions.cloud.hackathon.modules.orderflow.ai.api;

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
import vn.ezisolutions.cloud.hackathon.modules.orderflow.common.security.OrderFlowSecurity;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class OrderFlowAiController {
    private final OrderExtractionGateway extractionGateway;

    @PostMapping("/extract-order")
    public BaseResponse extractOrder(@Valid @RequestBody ExtractOrderRequest request) {
        var currentUser = OrderFlowSecurity.currentUser();
        return BaseResponse.success(extractionGateway.extract(
                new OrderExtractionCommand(currentUser.organizationId(), null, null, request.rawText())));
    }

    public record ExtractOrderRequest(@NotBlank String rawText) {
    }
}
