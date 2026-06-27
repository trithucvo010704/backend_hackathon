package vn.ezisolutions.cloud.hackathon.documents.ai_core;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "agent_brains")
@EntityListeners(AuditingEntityListener.class)
public class AgentBrain {
    @Id
    @GeneratedValue
    @UuidGenerator(style = UuidGenerator.Style.TIME)
    @Column(name = "id")
    @JsonSerialize(using = ToStringSerializer.class)
    private UUID id;

    private String name;

    @Column(name = "model_id")
    private UUID modelId;

    @Column(name = "env_id")
    private UUID envId;

    @Column(name = "system_instruction", columnDefinition = "TEXT")
    @Lob
    private String systemInstruction;

    private Double temperature;

    @Column(name = "thinking_level")
    private String thinkingLevel;

    @Column(name = "google_search")
    private Boolean googleSearch;

    @Column(name = "include_thoughts")
    private Boolean includeThoughts;

    @Column(name = "mcp_server_ids", columnDefinition = "TEXT")
    private String mcpServerIds;

    @Column(name = "prompt_template", columnDefinition = "TEXT")
    @Lob
    private String promptTemplate;

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

    @Transient
    private AiModel model;

    @Transient
    private AiEnv env;

    @Transient
    private List<McpServer> mcpServers;
}
