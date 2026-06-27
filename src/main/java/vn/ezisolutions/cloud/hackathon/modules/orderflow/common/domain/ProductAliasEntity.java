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
@Table(name = "product_aliases")
@EntityListeners(AuditingEntityListener.class)
public class ProductAliasEntity extends AuditableColumns {
    @Id
    @GeneratedValue
    @UuidGenerator(style = UuidGenerator.Style.TIME)
    private UUID id;

    @Column(name = "organization_id", nullable = false)
    private UUID organizationId;

    @Column(name = "alias_text", nullable = false)
    private String aliasText;

    @Column(name = "normalized_alias", nullable = false)
    private String normalizedAlias;

    @Column(name = "sku_id")
    private UUID skuId;

    @Column(name = "product_family")
    private String productFamily;

    private String material;
    private String brand;

    @Column(name = "diameter_mm")
    private BigDecimal diameterMm;

    @Column(name = "pressure_class")
    private String pressureClass;

    @Column(name = "fitting_type")
    private String fittingType;

    @Column(name = "thread_type")
    private String threadType;

    @Column(name = "confidence_weight", nullable = false)
    private BigDecimal confidenceWeight;

    private String note;
    private Boolean active;
}
