package vn.ezisolutions.cloud.hackathon.modules.orderflow.common.domain;

public enum DraftOrderStatus {
    NEW,
    EXTRACTING,
    READY_FOR_REVIEW,
    NEEDS_CLARIFICATION,
    ON_HOLD,
    APPROVED,
    REJECTED,
    EXPORTED
}
