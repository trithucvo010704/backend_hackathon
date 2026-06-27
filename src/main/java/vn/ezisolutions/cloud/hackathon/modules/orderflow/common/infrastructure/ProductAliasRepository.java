package vn.ezisolutions.cloud.hackathon.modules.orderflow.common.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.ezisolutions.cloud.hackathon.modules.orderflow.common.domain.ProductAliasEntity;

import java.util.List;
import java.util.UUID;

public interface ProductAliasRepository extends JpaRepository<ProductAliasEntity, UUID> {
    List<ProductAliasEntity> findByOrganizationIdAndActiveTrue(UUID organizationId);
}
