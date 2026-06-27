package vn.ezisolutions.cloud.hackathon.modules.orderflow.common.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "customer_credit_profiles")
public class CustomerCreditProfileEntity {
    @Id
    @Column(name = "customer_id")
    private UUID customerId;

    @Column(name = "credit_limit", nullable = false)
    private BigDecimal creditLimit;

    @Column(name = "current_debt", nullable = false)
    private BigDecimal currentDebt;

    @Column(name = "overdue_debt", nullable = false)
    private BigDecimal overdueDebt;

    @Column(name = "pending_approved_order_amount", nullable = false)
    private BigDecimal pendingApprovedOrderAmount;

    @Column(name = "payment_term_days", nullable = false)
    private Integer paymentTermDays;

    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;
}
