package vn.ezisolutions.cloud.hackathon.modules.orderflow.common.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.ezisolutions.cloud.hackathon.modules.orderflow.common.domain.ProductSkuEntity;

import java.util.List;
import java.util.UUID;

public interface ProductSkuRepository extends JpaRepository<ProductSkuEntity, UUID> {
    List<ProductSkuEntity> findByOrganizationIdAndActiveTrueOrderBySkuCode(UUID organizationId);
}
