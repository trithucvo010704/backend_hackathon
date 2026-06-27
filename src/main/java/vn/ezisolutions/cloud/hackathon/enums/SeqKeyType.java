package vn.ezisolutions.cloud.hackathon.enums;

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;
import lombok.Getter;

@Getter
public enum SeqKeyType {
    EPIC, STORY, TASK, REQ, RREQ, REVC, DSCR,
    @JsonEnumDefaultValue
    UNKNOWN;
}
