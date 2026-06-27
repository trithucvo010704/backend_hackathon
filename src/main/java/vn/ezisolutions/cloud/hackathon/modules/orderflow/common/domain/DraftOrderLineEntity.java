package vn.ezisolutions.cloud.hackathon.modules.orderflow.common.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.type.SqlTypes;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "draft_order_lines")
@EntityListeners(AuditingEntityListener.class)
public class DraftOrderLineEntity extends AuditableColumns {
    @Id
    @GeneratedValue
    @UuidGenerator(style = UuidGenerator.Style.TIME)
    private UUID id;

    @Column(name = "organization_id", nullable = false)
    private UUID organizationId;

    @Column(name = "draft_order_id", nullable = false)
    private UUID draftOrderId;

    @Column(name = "line_no", nullable = false)
    private Integer lineNo;

    @Column(name = "raw_line_text", nullable = false, columnDefinition = "TEXT")
    private String rawLineText;

    @Column(name = "item_description", columnDefinition = "TEXT")
    private String itemDescription;

    @Column(nullable = false)
    private BigDecimal quantity;

    @Column(name = "requested_unit")
    private String requestedUnit;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "extracted_attributes", nullable = false, columnDefinition = "jsonb")
    private Map<String, Object> extractedAttributes = new LinkedHashMap<>();

    @Column(name = "selected_sku_id")
    private UUID selectedSkuId;

    @Column(name = "selected_by_user_id")
    private UUID selectedByUserId;

    @Column(name = "selected_at")
    private OffsetDateTime selectedAt;

    @Column(name = "unit_price")
    private BigDecimal unitPrice;

    @Column(name = "price_source")
    private String priceSource;

    @Column(name = "line_amount")
    private BigDecimal lineAmount;

    @Column(name = "confidence_score")
    private BigDecimal confidenceScore;

    @Column(name = "clarification_question", columnDefinition = "TEXT")
    private String clarificationQuestion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DraftOrderLineStatus status;
}
