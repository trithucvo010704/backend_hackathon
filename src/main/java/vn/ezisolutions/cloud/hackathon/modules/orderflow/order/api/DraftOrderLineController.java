package vn.ezisolutions.cloud.hackathon.modules.orderflow.order.api;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.ezisolutions.cloud.hackathon.core.common.BaseResponse;
import vn.ezisolutions.cloud.hackathon.modules.orderflow.common.infrastructure.DraftOrderLineRepository;
import vn.ezisolutions.cloud.hackathon.modules.orderflow.common.security.OrderFlowSecurity;
import vn.ezisolutions.cloud.hackathon.modules.orderflow.matching.application.SkuMatchingService;
import vn.ezisolutions.cloud.hackathon.modules.orderflow.order.application.DraftOrderWorkflowService;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequestMapping("/api/draft-order-lines")
@RequiredArgsConstructor
public class DraftOrderLineController {
    private final DraftOrderWorkflowService workflowService;
    private final SkuMatchingService skuMatchingService;
    private final DraftOrderLineRepository lineRepository;

    @PatchMapping("/{lineId}")
    public BaseResponse updateLine(@PathVariable UUID lineId, @Valid @RequestBody UpdateLineRequest request) {
        return BaseResponse.success(workflowService.updateLine(OrderFlowSecurity.currentUser(), lineId, request.quantity(), request.requestedUnit(), request.itemDescription()));
    }

    @PostMapping("/{lineId}/match-skus")
    public BaseResponse matchSkus(@PathVariable UUID lineId) {
        return BaseResponse.success(skuMatchingService.matchLine(lineRepository.findById(lineId).orElseThrow()));
    }

    @GetMapping("/{lineId}/sku-candidates")
    public BaseResponse candidates(@PathVariable UUID lineId) {
        return BaseResponse.success(skuMatchingService.candidates(lineId));
    }

    @PostMapping("/{lineId}/select-sku")
    public BaseResponse selectSku(@PathVariable UUID lineId, @RequestBody SelectSkuRequest request) {
        return BaseResponse.success(workflowService.selectSku(OrderFlowSecurity.currentUser(), lineId, request.skuId()));
    }

    @PostMapping("/{lineId}/reject")
    public BaseResponse rejectLine(@PathVariable UUID lineId, @RequestBody(required = false) RejectLineRequest request) {
        return BaseResponse.success(workflowService.rejectLine(OrderFlowSecurity.currentUser(), lineId, request == null ? null : request.reason()));
    }

    public record UpdateLineRequest(BigDecimal quantity, String requestedUnit, String itemDescription) {
    }

    public record SelectSkuRequest(UUID skuId) {
    }

    public record RejectLineRequest(String reason) {
    }
}
