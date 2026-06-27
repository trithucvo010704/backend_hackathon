package vn.ezisolutions.cloud.hackathon.modules.orderflow.common.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name = "inventory_checks")
public class InventoryCheckEntity {
    @Id
    @GeneratedValue
    @UuidGenerator(style = UuidGenerator.Style.TIME)
    private UUID id;

    @Column(name = "organization_id", nullable = false)
    private UUID organizationId;

    @Column(name = "draft_order_line_id", nullable = false)
    private UUID draftOrderLineId;

    @Column(name = "warehouse_id", nullable = false)
    private UUID warehouseId;

    @Column(name = "sku_id", nullable = false)
    private UUID skuId;

    @Column(name = "requested_quantity")
    private BigDecimal requestedQuantity;

    @Column(name = "on_hand_quantity")
    private BigDecimal onHandQuantity;

    @Column(name = "reserved_quantity")
    private BigDecimal reservedQuantity;

    @Column(name = "available_quantity")
    private BigDecimal availableQuantity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RuleCheckStatus status;

    private String reason;

    @Column(name = "checked_at")
    private OffsetDateTime checkedAt;
}
