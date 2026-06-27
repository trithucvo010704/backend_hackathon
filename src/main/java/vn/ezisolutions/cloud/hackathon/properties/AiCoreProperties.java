package vn.ezisolutions.cloud.hackathon.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "ai-core")
public class AiCoreProperties {

    private int quotaCooldownMinutes = 30;
}
