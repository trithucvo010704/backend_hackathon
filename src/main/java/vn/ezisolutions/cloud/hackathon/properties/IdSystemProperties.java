package vn.ezisolutions.cloud.hackathon.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Component
@ConfigurationProperties(prefix = "id-system")
@Getter
@Setter
@Validated
public class IdSystemProperties extends HttpRestClientProperties {
}
