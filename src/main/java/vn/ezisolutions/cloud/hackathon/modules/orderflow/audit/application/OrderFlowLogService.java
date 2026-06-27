package vn.ezisolutions.cloud.hackathon.modules.orderflow.audit.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.ezisolutions.cloud.hackathon.modules.orderflow.common.domain.ActorType;
import vn.ezisolutions.cloud.hackathon.modules.orderflow.common.domain.AuditEventEntity;
import vn.ezisolutions.cloud.hackathon.modules.orderflow.common.domain.ProcessingEventEntity;
import vn.ezisolutions.cloud.hackathon.modules.orderflow.common.domain.ReviewActionEntity;
import vn.ezisolutions.cloud.hackathon.modules.orderflow.common.infrastructure.AuditEventRepository;
import vn.ezisolutions.cloud.hackathon.modules.orderflow.common.infrastructure.ProcessingEventRepository;
import vn.ezisolutions.cloud.hackathon.modules.orderflow.common.infrastructure.ReviewActionRepository;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderFlowLogService {
    private final ProcessingEventRepository processingEventRepository;
    private final AuditEventRepository auditEventRepository;
    private final ReviewActionRepository reviewActionRepository;

    public void processing(UUID organizationId, UUID draftOrderId, String stage, String status, Map<String, Object> metadata) {
        ProcessingEventEntity event = new ProcessingEventEntity();
        event.setOrganizationId(organizationId);
        event.setDraftOrderId(draftOrderId);
        event.setStage(stage);
        event.setStatus(status);
        event.setStartedAt(OffsetDateTime.now());
        event.setFinishedAt(OffsetDateTime.now());
        event.setDurationMs(0);
        event.setMetadata(metadata == null ? Map.of() : metadata);
        processingEventRepository.save(event);
    }

    public void audit(UUID organizationId, UUID draftOrderId, String aggregateType, UUID aggregateId,
                      ActorType actorType, UUID actorUserId, String eventType, Map<String, Object> afterData) {
        AuditEventEntity event = new AuditEventEntity();
        event.setOrganizationId(organizationId);
        event.setDraftOrderId(draftOrderId);
        event.setAggregateType(aggregateType);
        event.setAggregateId(aggregateId);
        event.setActorType(actorType);
        event.setActorUserId(actorUserId);
        event.setEventType(eventType);
        event.setAfterData(afterData == null ? Map.of() : afterData);
        event.setCreatedAt(OffsetDateTime.now());
        auditEventRepository.save(event);
    }

    public void review(UUID organizationId, UUID draftOrderId, UUID draftOrderLineId,
                       String actionType, String comment, UUID actorUserId, Map<String, Object> afterData) {
        ReviewActionEntity action = new ReviewActionEntity();
        action.setOrganizationId(organizationId);
        action.setDraftOrderId(draftOrderId);
        action.setDraftOrderLineId(draftOrderLineId);
        action.setActionType(actionType);
        action.setComment(comment);
        action.setActorUserId(actorUserId);
        action.setAfterData(afterData == null ? Map.of() : afterData);
        action.setCreatedAt(OffsetDateTime.now());
        reviewActionRepository.save(action);
    }
}
