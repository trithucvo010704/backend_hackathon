package vn.ezisolutions.cloud.hackathon.modules.orderflow.common.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.ezisolutions.cloud.hackathon.modules.orderflow.common.domain.ProcessingEventEntity;

import java.util.List;
import java.util.UUID;

public interface ProcessingEventRepository extends JpaRepository<ProcessingEventEntity, UUID> {
    List<ProcessingEventEntity> findByDraftOrderIdOrderByStartedAtDesc(UUID draftOrderId);
}
