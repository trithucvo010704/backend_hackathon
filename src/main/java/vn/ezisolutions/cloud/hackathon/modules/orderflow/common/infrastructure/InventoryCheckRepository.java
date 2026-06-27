package vn.ezisolutions.cloud.hackathon.modules.orderflow.common.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.ezisolutions.cloud.hackathon.modules.orderflow.common.domain.InventoryCheckEntity;

import java.util.List;
import java.util.UUID;

public interface InventoryCheckRepository extends JpaRepository<InventoryCheckEntity, UUID> {
    List<InventoryCheckEntity> findByDraftOrderLineId(UUID draftOrderLineId);
}
