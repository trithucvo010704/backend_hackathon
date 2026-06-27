package vn.ezisolutions.cloud.hackathon.modules.orderflow.common.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "inventory_balances")
public class InventoryBalanceEntity {
    @Id
    @GeneratedValue
    @UuidGenerator(style = UuidGenerator.Style.TIME)
    private UUID id;

    @Column(name = "organization_id", nullable = false)
    private UUID organizationId;

    @Column(name = "warehouse_id", nullable = false)
    private UUID warehouseId;

    @Column(name = "sku_id", nullable = false)
    private UUID skuId;

    @Column(name = "on_hand_quantity", nullable = false)
    private BigDecimal onHandQuantity;

    @Column(name = "reserved_quantity", nullable = false)
    private BigDecimal reservedQuantity;

    @Column(name = "available_quantity")
    private BigDecimal availableQuantity;

    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;
}
