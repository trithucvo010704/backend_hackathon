package vn.ezisolutions.cloud.hackathon.modules.orderflow.common.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.ezisolutions.cloud.hackathon.modules.orderflow.common.domain.SkuPriceEntity;

import java.util.List;
import java.util.UUID;

public interface SkuPriceRepository extends JpaRepository<SkuPriceEntity, UUID> {
    List<SkuPriceEntity> findByOrganizationIdAndSkuIdAndActiveTrueOrderByMinQuantityDesc(UUID organizationId, UUID skuId);
}
