package vn.ezisolutions.cloud.hackathon.documents.ai_core;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import vn.ezisolutions.cloud.hackathon.core.jpa.JsonConverters;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "agent_responses")
@EntityListeners(AuditingEntityListener.class)
public class AgentResponse {
    @Id
    @GeneratedValue
    @UuidGenerator(style = UuidGenerator.Style.TIME)
    @Column(name = "id")
    @JsonSerialize(using = ToStringSerializer.class)
    private UUID id;

    @Column(name = "agent_name")
    private String agentName;

    private String skill;

    @Column(name = "correlation_type")
    private String correlationType;

    @Column(name = "correlation_id")
    private UUID correlationId;

    @Column(name = "provider", length = 64)
    private String provider;

    @Column(name = "model_name")
    private String modelName;

    @Column(name = "duration_ms")
    private Integer durationMs;

    @Column(name = "finish_reason")
    private String finishReason;

    @Column(name = "brain_id")
    private UUID brainId;

    @Column(name = "prompt", columnDefinition = "TEXT")
    @Lob
    private String prompt;

    @Column(name = "input", columnDefinition = "TEXT")
    @Lob
    @Convert(converter = JsonConverters.ObjectMapConverter.class)
    private Map<String, Object> input;

    @Column(name = "output", columnDefinition = "TEXT")
    @Lob
    @Convert(converter = JsonConverters.ObjectMapConverter.class)
    private Map<String, Object> output;

    @Column(name = "output_text", columnDefinition = "TEXT")
    @Lob
    private String outputText;

    @Column(name = "tools", columnDefinition = "TEXT")
    @Lob
    private String tools;

    @Column(name = "error", columnDefinition = "TEXT")
    private String error;

    @CreatedDate
    @Column(name = "created_at")
    @JsonProperty("created_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    @JsonProperty("updated_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
}
