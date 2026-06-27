package vn.ezisolutions.cloud.hackathon.enums;

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;
import lombok.Getter;

@Getter
public enum ScrumIssuePriority {
    LOW,
    MEDIUM,
    HIGH,
    CRITICAL,
    @JsonEnumDefaultValue
    UNKNOWN;
}
