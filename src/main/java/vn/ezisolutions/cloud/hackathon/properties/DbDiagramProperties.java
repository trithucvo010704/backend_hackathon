package vn.ezisolutions.cloud.hackathon.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Component
@ConfigurationProperties(prefix = "dbdiagram")
@Getter
@Setter
@Validated
public class DbDiagramProperties extends HttpRestClientProperties {
}
