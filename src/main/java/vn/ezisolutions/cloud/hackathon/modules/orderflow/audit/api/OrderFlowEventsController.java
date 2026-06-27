package vn.ezisolutions.cloud.hackathon.modules.orderflow.audit.api;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.ezisolutions.cloud.hackathon.core.common.BaseResponse;
import vn.ezisolutions.cloud.hackathon.modules.orderflow.common.infrastructure.AuditEventRepository;
import vn.ezisolutions.cloud.hackathon.modules.orderflow.common.infrastructure.ProcessingEventRepository;
import vn.ezisolutions.cloud.hackathon.modules.orderflow.common.infrastructure.ReviewActionRepository;

import java.util.UUID;

@RestController
@RequestMapping("/api/draft-orders/{id}")
@RequiredArgsConstructor
public class OrderFlowEventsController {
    private final ProcessingEventRepository processingEventRepository;
    private final AuditEventRepository auditEventRepository;
    private final ReviewActionRepository reviewActionRepository;

    @GetMapping("/processing-events")
    public BaseResponse processingEvents(@PathVariable UUID id) {
        return BaseResponse.success(processingEventRepository.findByDraftOrderIdOrderByStartedAtDesc(id));
    }

    @GetMapping("/audit-events")
    public BaseResponse auditEvents(@PathVariable UUID id) {
        return BaseResponse.success(auditEventRepository.findByDraftOrderIdOrderByCreatedAtDesc(id));
    }

    @GetMapping("/review-actions")
    public BaseResponse reviewActions(@PathVariable UUID id) {
        return BaseResponse.success(reviewActionRepository.findByDraftOrderIdOrderByCreatedAtDesc(id));
    }
}
