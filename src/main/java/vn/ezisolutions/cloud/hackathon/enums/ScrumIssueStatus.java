package vn.ezisolutions.cloud.hackathon.enums;

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;
import lombok.Getter;

@Getter
public enum ScrumIssueStatus {
    BACKLOG, IN_PROGRESS, REVIEW, NEED_FIX, DONE, CANCEL, STORED, CLOSED,
    @JsonEnumDefaultValue
    UNKNOWN;
}
