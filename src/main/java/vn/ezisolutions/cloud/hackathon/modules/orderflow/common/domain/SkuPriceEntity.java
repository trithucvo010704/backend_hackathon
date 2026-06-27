package vn.ezisolutions.cloud.hackathon.modules.orderflow.common.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "sku_prices")
@EntityListeners(AuditingEntityListener.class)
public class SkuPriceEntity extends AuditableColumns {
    @Id
    @GeneratedValue
    @UuidGenerator(style = UuidGenerator.Style.TIME)
    private UUID id;

    @Column(name = "organization_id", nullable = false)
    private UUID organizationId;

    @Column(name = "price_list_id", nullable = false)
    private UUID priceListId;

    @Column(name = "sku_id", nullable = false)
    private UUID skuId;

    @Column(name = "min_quantity", nullable = false)
    private BigDecimal minQuantity;

    @Column(name = "unit_price", nullable = false)
    private BigDecimal unitPrice;

    @Column(name = "approval_floor_price")
    private BigDecimal approvalFloorPrice;

    private Boolean active;
}
