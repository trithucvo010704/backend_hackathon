package vn.ezisolutions.cloud.hackathon.modules.orderflow.ai.application;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.ezisolutions.cloud.hackathon.core.exceptions.CustomValidationException;
import vn.ezisolutions.cloud.hackathon.documents.ai_core.AgentBrain;
import vn.ezisolutions.cloud.hackathon.documents.ai_core.AiEnv;
import vn.ezisolutions.cloud.hackathon.documents.ai_core.AiModel;
import vn.ezisolutions.cloud.hackathon.documents.ai_core.AssistantAgent;
import vn.ezisolutions.cloud.hackathon.repositories.ai_core.AgentBrainRepository;
import vn.ezisolutions.cloud.hackathon.repositories.ai_core.AiEnvRepository;
import vn.ezisolutions.cloud.hackathon.repositories.ai_core.AiModelRepository;
import vn.ezisolutions.cloud.hackathon.repositories.ai_core.AssistantAgentRepository;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderFlowAgentRegistryService {
    public static final String ORDER_EXTRACTION_AGENT = "ORDER_EXTRACTION_AGENT";
    public static final String EXTRACT_SKILL = "extract";

    private final AssistantAgentRepository assistantAgentRepository;
    private final AgentBrainRepository agentBrainRepository;
    private final AiModelRepository aiModelRepository;
    private final AiEnvRepository aiEnvRepository;

    @Value("${orderflow.openai.api-key:}")
    private String fallbackApiKey;

    @Transactional(readOnly = true)
    public OrderFlowAgentRuntime resolveOpenAiRuntime(String agentName, String skill) {
        AssistantAgent agent = assistantAgentRepository.findFirstByName(agentName)
                .orElseThrow(() -> new CustomValidationException("Không tìm thấy agent MAS: " + agentName, null));
        String brainId = agent.getSkills() == null ? null : agent.getSkills().get(skill);
        if (brainId == null || brainId.isBlank()) {
            throw new CustomValidationException("Agent " + agentName + " chưa map skill: " + skill, null);
        }
        AgentBrain brain = agentBrainRepository.findById(UUID.fromString(brainId))
                .orElseThrow(() -> new CustomValidationException("Không tìm thấy agent brain: " + brainId, null));
        AiModel model = aiModelRepository.findById(brain.getModelId())
                .orElseThrow(() -> new CustomValidationException("Agent brain chưa cấu hình model", null));
        if (!"OPENAI".equalsIgnoreCase(model.getProvider())) {
            throw new CustomValidationException("OrderFlow MVP yêu cầu OPENAI provider, hiện tại là: " + model.getProvider(), null);
        }
        AiEnv env = resolveEnv(brain);
        String apiKey = text(env.getApiKey());
        if (apiKey.isBlank()) {
            apiKey = text(fallbackApiKey);
        }
        if (apiKey.isBlank()) {
            throw new CustomValidationException("OpenAI API key chưa được cấu hình trong ai_envs.api_key", null);
        }
        return new OrderFlowAgentRuntime(agentName, skill, agent, brain, model, env, apiKey);
    }

    @Transactional
    public void markSuccess(UUID envId) {
        aiEnvRepository.findById(envId).ifPresent(env -> {
            env.setLastUsedAt(LocalDateTime.now());
            env.setFailureCount(0);
            env.setLastError(null);
            aiEnvRepository.save(env);
        });
    }

    @Transactional
    public void markFailure(UUID envId, String error) {
        aiEnvRepository.findById(envId).ifPresent(env -> {
            env.setLastError(error);
            env.setFailureCount(env.getFailureCount() == null ? 1 : env.getFailureCount() + 1);
            aiEnvRepository.save(env);
        });
    }

    private AiEnv resolveEnv(AgentBrain brain) {
        if (brain.getEnvId() != null) {
            return aiEnvRepository.findById(brain.getEnvId())
                    .orElseThrow(() -> new CustomValidationException("Agent brain chưa cấu hình env hợp lệ", null));
        }
        return aiEnvRepository.findAvailable("OPENAI", LocalDateTime.now()).stream()
                .findFirst()
                .orElseThrow(() -> new CustomValidationException("Không tìm thấy ai_envs OPENAI đang enabled", null));
    }

    private String text(String value) {
        return value == null ? "" : value.trim();
    }
}
