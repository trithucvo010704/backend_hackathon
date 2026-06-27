package vn.ezisolutions.cloud.hackathon.modules.orderflow.common.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.ezisolutions.cloud.hackathon.modules.orderflow.common.domain.DraftOrderEntity;
import vn.ezisolutions.cloud.hackathon.modules.orderflow.common.domain.DraftOrderStatus;

import java.util.List;
import java.util.UUID;

public interface DraftOrderRepository extends JpaRepository<DraftOrderEntity, UUID> {
    List<DraftOrderEntity> findByOrganizationIdOrderByCreatedAtDesc(UUID organizationId);
    List<DraftOrderEntity> findByOrganizationIdAndStatusOrderByCreatedAtDesc(UUID organizationId, DraftOrderStatus status);
    long countByOrganizationId(UUID organizationId);
}
