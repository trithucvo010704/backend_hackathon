package vn.ezisolutions.cloud.hackathon.modules.orderflow.common.domain;

public enum DraftOrderLineStatus {
    EXTRACTED,
    PENDING_MATCH,
    NEEDS_CLARIFICATION,
    MATCHED,
    ON_HOLD,
    APPROVED,
    REJECTED
}
