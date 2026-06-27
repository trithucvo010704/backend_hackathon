package vn.ezisolutions.cloud.hackathon.modules.orderflow.common.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.ezisolutions.cloud.hackathon.modules.orderflow.common.domain.PriceListEntity;

import java.util.List;
import java.util.UUID;

public interface PriceListRepository extends JpaRepository<PriceListEntity, UUID> {
    List<PriceListEntity> findByOrganizationIdAndActiveTrueOrderByPriorityAsc(UUID organizationId);
}
