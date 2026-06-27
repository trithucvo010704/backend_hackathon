package vn.ezisolutions.cloud.hackathon.configs;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpRequest;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClient;
import vn.ezisolutions.cloud.hackathon.properties.*;

import java.io.IOException;
import java.net.http.HttpClient;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Base64;

@Configuration
@Slf4j
@ConditionalOnClass(RestClientProperties.class)
public class RestClientConfig {

    @Bean
    public RestClient defaultRestClient(RestClientProperties properties) {
        return createRestClientBuilder(properties)
                .defaultHeader("Authorization", "Bearer %s".formatted(properties.getToken()))
                .build();
    }

    @Bean
    public RestClient idSystemRestClient(IdSystemProperties idSystemProperties) {
        return createRestClientBuilder(idSystemProperties)
                .defaultHeader("Authorization", "Bearer %s".formatted(idSystemProperties.getToken()))
                .build();
    }

    @Bean
    public RestClient dbDiagramRestClient(DbDiagramProperties properties) {
        return createRestClientBuilder(properties)
                .defaultHeader("dbdiagram-access-token", properties.getToken())
                .build();
    }

    @Bean
    public RestClient bitbucketRestClient(BitbucketProperties properties) {
        String token = Base64.getEncoder().encodeToString("%s:%s".formatted(properties.getUsername(), properties.getToken()).getBytes());
        return createRestClientBuilder(properties)
                .defaultHeader("Authorization", "Basic %s".formatted(token))
                .build();
    }


    private ClientHttpRequestFactory createHttpRequestFactory(HttpRestClientProperties properties) {
        JdkClientHttpRequestFactory factory = new JdkClientHttpRequestFactory(HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(properties.getConnectTimeout()))
                .build());
        factory.setReadTimeout(Duration.ofSeconds(properties.getReadTimeout()));
        return factory;
    }

    private RestClient.Builder createRestClientBuilder(HttpRestClientProperties properties) {
        return RestClient.builder()
                .baseUrl(properties.getBaseUrl())
                .requestFactory(createHttpRequestFactory(properties))
                .defaultHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader("Accept", MediaType.APPLICATION_JSON_VALUE)
                .requestInterceptor(loggingInterceptor())
                .requestInterceptor(retryInterceptor(properties));
    }


    private ClientHttpRequestInterceptor loggingInterceptor() {
        return (request, body, execution) -> {
            logRequest(request, body);
            ClientHttpResponse response = execution.execute(request, body);
            return logResponse(request, response);
        };
    }

    private void logRequest(HttpRequest request, byte[] body) {
        log.info("Request: {} {}, Body: {}",
                request.getMethod(),
                request.getURI(),
                new String(body, StandardCharsets.UTF_8));
    }

    private ClientHttpResponse logResponse(HttpRequest request, ClientHttpResponse response) throws IOException {
        byte[] responseBody = StreamUtils.copyToByteArray(response.getBody());
        log.info("Response: {} {}, Status: {}, Body: {}",
                request.getMethod(),
                request.getURI(),
                response.getStatusCode(),
                new String(responseBody, StandardCharsets.UTF_8));
        return new BufferingClientHttpResponseWrapper(response, responseBody);
    }

    private ClientHttpRequestInterceptor retryInterceptor(HttpRestClientProperties properties) {
        RetryTemplate retryTemplate = new RetryTemplate();
        SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy(properties.getMaxRetries());
        retryTemplate.setRetryPolicy(retryPolicy);

        FixedBackOffPolicy backOffPolicy = new FixedBackOffPolicy();
        backOffPolicy.setBackOffPeriod(properties.getRetryDelay() * 1000);
        retryTemplate.setBackOffPolicy(backOffPolicy);

        return (request, body, execution) -> {
            try {
                return retryTemplate.execute(context -> {
                    ClientHttpResponse response = execution.execute(request, body);
                    if (response.getStatusCode().is5xxServerError()) {
                        throw new HttpServerErrorException(response.getStatusCode(), "Server error, retrying...");
                    }

                    return response;
                });
            } catch (Exception e) {
                log.error("Retry failed for {} {}: {}", request.getMethod(), request.getURI(), e.getMessage());
                throw e;
            }
        };
    }

    private static class BufferingClientHttpResponseWrapper implements ClientHttpResponse {
        private final ClientHttpResponse response;
        private final byte[] body;

        public BufferingClientHttpResponseWrapper(ClientHttpResponse response, byte[] body) {
            this.response = response;
            this.body = body;
        }

        @Override
        public org.springframework.http.HttpStatusCode getStatusCode() throws IOException {
            return response.getStatusCode();
        }

        @Override
        public String getStatusText() throws IOException {
            return response.getStatusText();
        }

        @Override
        public void close() {
            response.close();
        }

        @Override
        public java.io.InputStream getBody() {
            return new java.io.ByteArrayInputStream(body);
        }

        @Override
        public org.springframework.http.HttpHeaders getHeaders() {
            return response.getHeaders();
        }
    }

}
