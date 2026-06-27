package vn.ezisolutions.cloud.hackathon.enums;

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;
import lombok.Getter;

@Getter
public enum RefType {
    PROJECT, STORY, EPIC, TASK, GUIDELINE, REPOSITORY,
    @JsonEnumDefaultValue UNKNOWN;
}
