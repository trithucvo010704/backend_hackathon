package vn.ezisolutions.cloud.hackathon.modules.orderflow.common.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "sku_candidates")
public class SkuCandidateEntity {
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

    @Column(name = "rank_no", nullable = false)
    private Integer rankNo;

    @Column(name = "confidence_score", nullable = false)
    private BigDecimal confidenceScore;

    @Column(name = "match_reason", columnDefinition = "TEXT")
    private String matchReason;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "matched_attributes", nullable = false, columnDefinition = "jsonb")
    private Map<String, Object> matchedAttributes = new LinkedHashMap<>();

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "missing_attributes", nullable = false, columnDefinition = "jsonb")
    private Map<String, Object> missingAttributes = new LinkedHashMap<>();

    @Column(nullable = false)
    private String source;

    @Column(name = "created_at")
    private OffsetDateTime createdAt;
}
