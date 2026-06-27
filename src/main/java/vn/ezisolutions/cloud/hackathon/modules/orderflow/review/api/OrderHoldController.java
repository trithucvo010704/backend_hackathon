package vn.ezisolutions.cloud.hackathon.modules.orderflow.review.api;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.ezisolutions.cloud.hackathon.core.common.BaseResponse;
import vn.ezisolutions.cloud.hackathon.modules.orderflow.common.infrastructure.OrderHoldRepository;
import vn.ezisolutions.cloud.hackathon.modules.orderflow.common.security.OrderFlowSecurity;
import vn.ezisolutions.cloud.hackathon.modules.orderflow.order.application.DraftOrderWorkflowService;

import java.util.UUID;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class OrderHoldController {
    private final OrderHoldRepository orderHoldRepository;
    private final DraftOrderWorkflowService workflowService;

    @GetMapping("/draft-orders/{id}/holds")
    public BaseResponse holds(@PathVariable UUID id) {
        return BaseResponse.success(orderHoldRepository.findByDraftOrderIdOrderByCreatedAtDesc(id));
    }

    @PostMapping("/order-holds/{holdId}/release")
    public BaseResponse release(@PathVariable UUID holdId, @RequestBody(required = false) HoldActionRequest request) {
        return BaseResponse.success(workflowService.releaseHold(OrderFlowSecurity.currentUser(), holdId, request == null ? null : request.note()));
    }

    @PostMapping("/order-holds/{holdId}/reject")
    public BaseResponse reject(@PathVariable UUID holdId, @RequestBody(required = false) HoldActionRequest request) {
        return BaseResponse.success(workflowService.rejectHold(OrderFlowSecurity.currentUser(), holdId, request == null ? null : request.note()));
    }

    public record HoldActionRequest(String note) {
    }
}
