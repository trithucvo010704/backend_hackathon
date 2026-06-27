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
@Table(name = "credit_checks")
public class CreditCheckEntity {
    @Id
    @GeneratedValue
    @UuidGenerator(style = UuidGenerator.Style.TIME)
    private UUID id;

    @Column(name = "organization_id", nullable = false)
    private UUID organizationId;

    @Column(name = "draft_order_id", nullable = false)
    private UUID draftOrderId;

    @Column(name = "customer_id", nullable = false)
    private UUID customerId;

    @Column(name = "order_amount")
    private BigDecimal orderAmount;

    @Column(name = "credit_limit")
    private BigDecimal creditLimit;

    @Column(name = "current_debt")
    private BigDecimal currentDebt;

    @Column(name = "overdue_debt")
    private BigDecimal overdueDebt;

    @Column(name = "pending_approved_order_amount")
    private BigDecimal pendingApprovedOrderAmount;

    @Column(name = "projected_debt")
    private BigDecimal projectedDebt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RuleCheckStatus status;

    private String reason;

    @Column(name = "checked_at")
    private OffsetDateTime checkedAt;
}
