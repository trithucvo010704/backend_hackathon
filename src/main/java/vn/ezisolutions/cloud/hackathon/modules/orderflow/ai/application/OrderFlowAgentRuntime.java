package vn.ezisolutions.cloud.hackathon.modules.orderflow.ai.application;

import vn.ezisolutions.cloud.hackathon.documents.ai_core.AgentBrain;
import vn.ezisolutions.cloud.hackathon.documents.ai_core.AiEnv;
import vn.ezisolutions.cloud.hackathon.documents.ai_core.AiModel;
import vn.ezisolutions.cloud.hackathon.documents.ai_core.AssistantAgent;

public record OrderFlowAgentRuntime(
        String agentName,
        String skill,
        AssistantAgent agent,
        AgentBrain brain,
        AiModel model,
        AiEnv env,
        String apiKey
) {
    public String modelName() {
        return model.getName();
    }

    public String provider() {
        return model.getProvider();
    }

    public String systemInstruction() {
        return brain.getSystemInstruction() == null ? "" : brain.getSystemInstruction();
    }

    public String promptTemplate() {
        return brain.getPromptTemplate() == null ? "" : brain.getPromptTemplate();
    }
}
