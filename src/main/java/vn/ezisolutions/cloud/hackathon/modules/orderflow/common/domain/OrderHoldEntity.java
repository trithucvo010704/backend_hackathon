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

import java.time.OffsetDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "order_holds")
@EntityListeners(AuditingEntityListener.class)
public class OrderHoldEntity extends AuditableColumns {
    @Id
    @GeneratedValue
    @UuidGenerator(style = UuidGenerator.Style.TIME)
    private UUID id;

    @Column(name = "organization_id", nullable = false)
    private UUID organizationId;

    @Column(name = "draft_order_id", nullable = false)
    private UUID draftOrderId;

    @Column(name = "draft_order_line_id")
    private UUID draftOrderLineId;

    @Enumerated(EnumType.STRING)
    @Column(name = "hold_type", nullable = false)
    private HoldType holdType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private HoldStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private HoldSeverity severity;

    @Column(name = "rule_code")
    private String ruleCode;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String reason;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> payload = new LinkedHashMap<>();

    @Enumerated(EnumType.STRING)
    @Column(name = "created_by_actor_type", nullable = false)
    private ActorType createdByActorType;

    @Column(name = "released_by_user_id")
    private UUID releasedByUserId;

    @Column(name = "released_at")
    private OffsetDateTime releasedAt;

    @Column(name = "release_note")
    private String releaseNote;
}
