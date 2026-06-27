package vn.ezisolutions.cloud.hackathon.modules.orderflow.ai.application;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import vn.ezisolutions.cloud.hackathon.documents.ai_core.AiEnv;
import vn.ezisolutions.cloud.hackathon.repositories.ai_core.AiEnvRepository;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "orderflow.openai.persist-api-key", havingValue = "true")
public class OrderFlowOpenAiKeyInitializer implements ApplicationRunner {
    private final AiEnvRepository aiEnvRepository;

    @Value("${orderflow.openai.api-key:}")
    private String apiKey;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        String key = apiKey == null ? "" : apiKey.trim();
        if (key.isBlank()) {
            throw new IllegalStateException("ORDERFLOW_OPENAI_PERSIST_API_KEY=true but OPENAI_API_KEY is empty");
        }
        AiEnv env = aiEnvRepository.findAvailable("OPENAI", LocalDateTime.now()).stream()
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No enabled OPENAI ai_envs row found"));
        if (key.equals(env.getApiKey())) {
            return;
        }
        env.setApiKey(key);
        env.setLastError(null);
        env.setFailureCount(0);
        aiEnvRepository.save(env);
    }
}
