package vn.ezisolutions.cloud.hackathon.modules.orderflow.common.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.ezisolutions.cloud.hackathon.modules.orderflow.common.domain.DraftOrderLineEntity;

import java.util.List;
import java.util.UUID;

public interface DraftOrderLineRepository extends JpaRepository<DraftOrderLineEntity, UUID> {
    List<DraftOrderLineEntity> findByDraftOrderIdOrderByLineNo(UUID draftOrderId);
}
