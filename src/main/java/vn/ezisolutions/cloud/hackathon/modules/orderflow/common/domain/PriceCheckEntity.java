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
@Table(name = "price_checks")
public class PriceCheckEntity {
    @Id
    @GeneratedValue
    @UuidGenerator(style = UuidGenerator.Style.TIME)
    private UUID id;

    @Column(name = "organization_id", nullable = false)
    private UUID organizationId;

    @Column(name = "draft_order_line_id", nullable = false)
    private UUID draftOrderLineId;

    @Column(name = "sku_id", nullable = false)
    private UUID skuId;

    @Column(name = "price_list_id")
    private UUID priceListId;

    private BigDecimal quantity;

    @Column(name = "proposed_unit_price")
    private BigDecimal proposedUnitPrice;

    @Column(name = "reference_unit_price")
    private BigDecimal referenceUnitPrice;

    @Column(name = "approval_floor_price")
    private BigDecimal approvalFloorPrice;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RuleCheckStatus status;

    private String reason;

    @Column(name = "checked_at")
    private OffsetDateTime checkedAt;
}
