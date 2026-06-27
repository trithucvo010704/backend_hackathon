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
@Table(name = "inventory_reservations")
public class InventoryReservationEntity {
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

    @Column(name = "draft_order_line_id", nullable = false)
    private UUID draftOrderLineId;

    private BigDecimal quantity;

    @Enumerated(EnumType.STRING)
    private ReservationStatus status;

    @Column(name = "reserved_at")
    private OffsetDateTime reservedAt;

    @Column(name = "released_at")
    private OffsetDateTime releasedAt;

    @Column(name = "consumed_at")
    private OffsetDateTime consumedAt;
}
