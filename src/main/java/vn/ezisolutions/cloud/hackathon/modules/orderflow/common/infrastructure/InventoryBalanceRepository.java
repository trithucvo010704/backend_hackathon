package vn.ezisolutions.cloud.hackathon.modules.orderflow.common.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.ezisolutions.cloud.hackathon.modules.orderflow.common.domain.InventoryBalanceEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface InventoryBalanceRepository extends JpaRepository<InventoryBalanceEntity, UUID> {
    List<InventoryBalanceEntity> findByOrganizationId(UUID organizationId);
    Optional<InventoryBalanceEntity> findByOrganizationIdAndWarehouseIdAndSkuId(UUID organizationId, UUID warehouseId, UUID skuId);
}
