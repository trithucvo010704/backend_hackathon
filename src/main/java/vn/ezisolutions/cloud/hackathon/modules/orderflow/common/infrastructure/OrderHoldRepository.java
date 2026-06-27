package vn.ezisolutions.cloud.hackathon.modules.orderflow.common.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.ezisolutions.cloud.hackathon.modules.orderflow.common.domain.HoldStatus;
import vn.ezisolutions.cloud.hackathon.modules.orderflow.common.domain.OrderHoldEntity;

import java.util.List;
import java.util.UUID;

public interface OrderHoldRepository extends JpaRepository<OrderHoldEntity, UUID> {
    List<OrderHoldEntity> findByDraftOrderIdOrderByCreatedAtDesc(UUID draftOrderId);
    List<OrderHoldEntity> findByDraftOrderIdAndStatus(UUID draftOrderId, HoldStatus status);
    boolean existsByDraftOrderIdAndStatus(UUID draftOrderId, HoldStatus status);
}
