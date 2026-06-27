package vn.ezisolutions.cloud.hackathon.modules.orderflow.common.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import vn.ezisolutions.cloud.hackathon.modules.orderflow.common.domain.SkuCandidateEntity;

import java.util.List;
import java.util.UUID;

public interface SkuCandidateRepository extends JpaRepository<SkuCandidateEntity, UUID> {
    List<SkuCandidateEntity> findByDraftOrderLineIdOrderByRankNo(UUID draftOrderLineId);

    @Modifying
    void deleteByDraftOrderLineId(UUID draftOrderLineId);
}
