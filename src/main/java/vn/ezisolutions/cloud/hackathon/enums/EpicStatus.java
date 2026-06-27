package vn.ezisolutions.cloud.hackathon.enums;

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;
import lombok.Getter;

@Getter
public enum EpicStatus {
    OPEN,
    IN_PROGRESS,
    DONE,
    REOPENED,
    ARCHIVED,
    @JsonEnumDefaultValue
    UNKNOWN;
}