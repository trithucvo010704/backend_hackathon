package vn.ezisolutions.cloud.hackathon.services.ai_core;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.ezisolutions.cloud.hackathon.documents.ai_core.AiEnv;
import vn.ezisolutions.cloud.hackathon.properties.AiCoreProperties;
import vn.ezisolutions.cloud.hackathon.repositories.ai_core.AiEnvRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AiEnvSelectorService {
    private final AiEnvRepository aiEnvRepository;
    private final AiCoreProperties properties;

    @Transactional
    public AiEnv select(String provider, List<String> attemptedEnvIds) {
        List<AiEnv> candidates = aiEnvRepository.findAvailable(provider, LocalDateTime.now());
        return candidates.stream()
                .filter(env -> attemptedEnvIds == null || !attemptedEnvIds.contains(env.getId().toString()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No available AI env for provider: " + provider));
    }

    @Transactional
    public void markSuccess(AiEnv env) {
        env.setLastUsedAt(LocalDateTime.now());
        env.setFailureCount(0);
        env.setLastError(null);
        env.setCooldownUntil(null);
        aiEnvRepository.save(env);
    }

    @Transactional
    public void markFailure(AiEnv env, Exception exception) {
        int nextFailureCount = env.getFailureCount() == null ? 1 : env.getFailureCount() + 1;
        env.setFailureCount(nextFailureCount);
        env.setLastUsedAt(LocalDateTime.now());
        env.setLastError(exception.getMessage());
        if (isQuotaOrAuthFailure(exception)) {
            env.setCooldownUntil(LocalDateTime.now().plusMinutes(Math.max(1, properties.getQuotaCooldownMinutes())));
        }
        aiEnvRepository.save(env);
    }

    public boolean isQuotaOrAuthFailure(Exception exception) {
        Throwable cursor = exception;
        while (cursor != null) {
            String message = cursor.getMessage() == null ? "" : cursor.getMessage().toLowerCase();
            if (message.contains("429")
                    || message.contains("quota")
                    || message.contains("resource_exhausted")
                    || message.contains("401")
                    || message.contains("403")
                    || message.contains("503")
                    || message.contains("api key")
                    || message.contains("overloaded")
                    || message.contains("unavailable")
                    || message.contains("permission_denied")
                    || message.contains("unauthenticated")) {
                return true;
            }
            cursor = cursor.getCause();
        }
        return false;
    }
}
