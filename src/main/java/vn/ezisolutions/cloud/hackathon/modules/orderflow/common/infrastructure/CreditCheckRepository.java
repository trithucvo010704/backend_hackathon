package vn.ezisolutions.cloud.hackathon.modules.orderflow.common.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.ezisolutions.cloud.hackathon.modules.orderflow.common.domain.CreditCheckEntity;

import java.util.List;
import java.util.UUID;

public interface CreditCheckRepository extends JpaRepository<CreditCheckEntity, UUID> {
    List<CreditCheckEntity> findByDraftOrderId(UUID draftOrderId);
}
