package vn.ezisolutions.cloud.hackathon.modules.orderflow.common.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.ezisolutions.cloud.hackathon.modules.orderflow.common.domain.ReviewActionEntity;

import java.util.List;
import java.util.UUID;

public interface ReviewActionRepository extends JpaRepository<ReviewActionEntity, UUID> {
    List<ReviewActionEntity> findByDraftOrderIdOrderByCreatedAtDesc(UUID draftOrderId);
}
