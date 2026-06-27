package vn.ezisolutions.cloud.hackathon.modules.orderflow.common.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.ezisolutions.cloud.hackathon.modules.orderflow.common.domain.PriceCheckEntity;

import java.util.List;
import java.util.UUID;

public interface PriceCheckRepository extends JpaRepository<PriceCheckEntity, UUID> {
    List<PriceCheckEntity> findByDraftOrderLineId(UUID draftOrderLineId);
}
