package vn.ezisolutions.cloud.hackathon.core.dto;


import com.fasterxml.jackson.databind.JsonNode;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KafkaSystemEvent {
    protected SupportedEventAction action;
    protected JsonNode payload;

    @Getter
    public enum SupportedEventAction {

        EPIC_RELEASED("epic.changed"),
        STORY_RELEASED("story.released"),
        TASK_RELEASED("task.released"),
        DOCUMENT_EDITED("document.edited"),
        DOCUMENT_DELETED("document.deleted");

        private final String value;

        SupportedEventAction(String value) {
            this.value = value;
        }

        public static SupportedEventAction fromValue(String value) {
            for (SupportedEventAction status : SupportedEventAction.values()) {
                if (status.value.equals(value)) {
                    return status;
                }
            }
            throw new IllegalArgumentException("Invalid action value: " + value);
        }
    }
}
