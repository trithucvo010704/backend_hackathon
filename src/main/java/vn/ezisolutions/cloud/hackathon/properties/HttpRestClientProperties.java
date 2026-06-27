package vn.ezisolutions.cloud.hackathon.properties;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.validation.annotation.Validated;

@Getter
@Setter
@Validated
public class HttpRestClientProperties {
    @NotBlank
    private String baseUrl;
    private String token;
    private Integer connectTimeout = 60;
    private Integer readTimeout = 60;
    private Integer maxRetries = 4;
    private Integer retryDelay = 5;
}
