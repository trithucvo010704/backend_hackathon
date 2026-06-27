package vn.ezisolutions.cloud.hackathon.enums;

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;
import lombok.Getter;

@Getter
public enum RequestReviewer {
    LINA,
    MATTIN,
    DAVID,
    BOB,
    ROBIN,
    SARAH,
    KEVIN,
    LUX,
    ZORO,
    LEADER,
    @JsonEnumDefaultValue
    UNKNOWN;
}