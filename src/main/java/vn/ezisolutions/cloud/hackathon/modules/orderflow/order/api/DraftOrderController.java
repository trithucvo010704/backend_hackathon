package vn.ezisolutions.cloud.hackathon.modules.orderflow.order.api;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import vn.ezisolutions.cloud.hackathon.core.common.BaseResponse;
import vn.ezisolutions.cloud.hackathon.modules.orderflow.common.domain.DraftOrderStatus;
import vn.ezisolutions.cloud.hackathon.modules.orderflow.common.security.OrderFlowSecurity;
import vn.ezisolutions.cloud.hackathon.modules.orderflow.order.application.DraftOrderWorkflowService;

import java.util.UUID;

@RestController
@RequestMapping("/api/draft-orders")
@RequiredArgsConstructor
public class DraftOrderController {
    private final DraftOrderWorkflowService workflowService;

    @PostMapping("/from-text")
    public BaseResponse createFromText(@Valid @RequestBody CreateDraftOrderRequest request) {
        return BaseResponse.success(workflowService.createFromText(
                OrderFlowSecurity.currentUser(),
                new DraftOrderWorkflowService.CreateDraftOrderCommand(
                        request.customerId(), request.projectId(), request.warehouseId(), request.rawText())));
    }

    @GetMapping
    public BaseResponse list(@RequestParam(required = false) DraftOrderStatus status) {
        return BaseResponse.success(workflowService.list(OrderFlowSecurity.currentUser(), status));
    }

    @GetMapping("/{id}")
    public BaseResponse detail(@PathVariable UUID id) {
        return BaseResponse.success(workflowService.detail(id));
    }

    @PostMapping("/{id}/run-checks")
    public BaseResponse runChecks(@PathVariable UUID id) {
        workflowService.runChecks(id);
        return BaseResponse.success(workflowService.detail(id));
    }

    @PostMapping("/{id}/approve")
    public BaseResponse approve(@PathVariable UUID id) {
        return BaseResponse.success(workflowService.approve(OrderFlowSecurity.currentUser(), id));
    }

    @PostMapping("/{id}/reject")
    public BaseResponse reject(@PathVariable UUID id, @RequestBody(required = false) RejectRequest request) {
        return BaseResponse.success(workflowService.reject(OrderFlowSecurity.currentUser(), id, request == null ? null : request.reason()));
    }

    @PostMapping("/{id}/rerun-extraction")
    public BaseResponse rerunExtraction(@PathVariable UUID id) {
        return BaseResponse.fail("Rerun extraction is planned after prompt iteration; use create-from-text for MVP smoke.");
    }

    @PatchMapping("/{id}")
    public BaseResponse updateHeader(@PathVariable UUID id) {
        return BaseResponse.success(workflowService.detail(id));
    }

    public record CreateDraftOrderRequest(@NotNull UUID customerId, UUID projectId, @NotNull UUID warehouseId,
                                          @NotBlank String rawText) {
    }

    public record RejectRequest(String reason) {
    }
}
