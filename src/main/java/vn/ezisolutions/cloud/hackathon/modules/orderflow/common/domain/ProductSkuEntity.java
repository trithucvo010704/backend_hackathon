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
@Table(name = "product_skus")
@EntityListeners(AuditingEntityListener.class)
public class ProductSkuEntity extends AuditableColumns {
    @Id
    @GeneratedValue
    @UuidGenerator(style = UuidGenerator.Style.TIME)
    private UUID id;

    @Column(name = "organization_id", nullable = false)
    private UUID organizationId;

    @Column(name = "sku_code", nullable = false, length = 80)
    private String skuCode;

    @Column(name = "product_name", nullable = false)
    private String productName;

    @Column(name = "product_family", nullable = false)
    private String productFamily;

    private String material;
    private String brand;

    @Column(name = "diameter_mm")
    private BigDecimal diameterMm;

    @Column(name = "nominal_size")
    private String nominalSize;

    @Column(name = "size_system")
    private String sizeSystem;

    @Column(name = "pressure_class")
    private String pressureClass;

    @Column(name = "thickness_mm")
    private BigDecimal thicknessMm;

    @Column(name = "fitting_type")
    private String fittingType;

    @Column(name = "angle_degree")
    private Integer angleDegree;

    @Column(name = "thread_type")
    private String threadType;

    @Column(name = "reducer_from_mm")
    private BigDecimal reducerFromMm;

    @Column(name = "reducer_to_mm")
    private BigDecimal reducerToMm;

    @Column(name = "length_m")
    private BigDecimal lengthM;

    @Column(name = "sell_unit", nullable = false)
    private String sellUnit;

    @Column(name = "base_unit", nullable = false)
    private String baseUnit;

    @Column(name = "units_per_sell_unit", nullable = false)
    private BigDecimal unitsPerSellUnit;

    @Column(nullable = false)
    private Boolean active;
}
