package vn.ezisolutions.cloud.hackathon.configs;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaAuditing
@EnableJpaRepositories(basePackages = "vn.ezisolutions.cloud.hackathon.repositories")
public class JpaConfig {
}
