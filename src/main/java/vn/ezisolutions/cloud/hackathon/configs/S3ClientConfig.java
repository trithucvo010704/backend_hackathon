package vn.ezisolutions.cloud.hackathon.configs;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import vn.ezisolutions.cloud.hackathon.properties.S3ClientProperties;

import java.net.URI;

@Configuration
@ConditionalOnProperty(name = "s3-client.enabled", havingValue = "true", matchIfMissing = true)
public class S3ClientConfig {
    @Bean
    public S3Client s3Client(S3ClientProperties properties) {
        return S3Client.builder()
                .endpointOverride(URI.create(properties.getEndpoint()))
                .credentialsProvider(StaticCredentialsProvider.create(
                                AwsBasicCredentials.create(properties.getAccessKeyId(), properties.getSecretAccessKey())
                        )
                )
                .region(Region.of(properties.getRegion()))
                .serviceConfiguration(S3Configuration.builder()
                        .pathStyleAccessEnabled(true)
                        .build())
                .build();
    }
}
