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
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "ai_envs")
@EntityListeners(AuditingEntityListener.class)
public class AiEnv {
    @Id
    @GeneratedValue
    @UuidGenerator(style = UuidGenerator.Style.TIME)
    @Column(name = "id")
    @JsonSerialize(using = ToStringSerializer.class)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, length = 64)
    private String provider;

    @Column(name = "service_account_json", columnDefinition = "TEXT")
    @Lob
    private String serviceAccountJson;

    @Column(name = "production_mode", nullable = false)
    private Boolean productionMode;

    @Column(name = "api_key", columnDefinition = "TEXT")
    private String apiKey;

    private String location;

    @Column(name = "project_id")
    private String projectId;

    @Column(nullable = false)
    private Boolean enabled;

    @Column(nullable = false)
    private Integer priority;

    @Column(name = "last_used_at")
    private LocalDateTime lastUsedAt;

    @Column(name = "cooldown_until")
    private LocalDateTime cooldownUntil;

    @Column(name = "failure_count", nullable = false)
    private Integer failureCount;

    @Column(name = "last_error", columnDefinition = "TEXT")
    private String lastError;

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
