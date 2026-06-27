package vn.ezisolutions.cloud.hackathon.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Component
@ConfigurationProperties(prefix = "s3-client")
@Getter
@Setter
@Validated
public class S3ClientProperties {
    private String endpoint;
    private String accessKeyId;
    private String secretAccessKey;
    private String region;
    private String bucket;
    private String publicUrl;
}
