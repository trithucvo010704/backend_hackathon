package vn.ezisolutions.cloud.hackathon.modules.orderflow.ai.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import vn.ezisolutions.cloud.hackathon.documents.ai_core.AgentResponse;
import vn.ezisolutions.cloud.hackathon.modules.orderflow.ai.domain.OrderExtractionCommand;
import vn.ezisolutions.cloud.hackathon.repositories.ai_core.AgentResponseRepository;

import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderFlowAgentResponseLogService {
    private final AgentResponseRepository agentResponseRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void success(OrderFlowAgentRuntime runtime, OrderExtractionCommand command, String prompt,
                        Map<String, Object> input, Map<String, Object> output, String outputText, long durationMs) {
        AgentResponse response = base(runtime, command, prompt, input, durationMs);
        response.setOutput(output);
        response.setOutputText(outputText);
        agentResponseRepository.save(response);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void failure(OrderFlowAgentRuntime runtime, OrderExtractionCommand command, String prompt,
                        Map<String, Object> input, String error, long durationMs) {
        AgentResponse response = base(runtime, command, prompt, input, durationMs);
        response.setError(error);
        agentResponseRepository.save(response);
    }

    private AgentResponse base(OrderFlowAgentRuntime runtime, OrderExtractionCommand command, String prompt,
                               Map<String, Object> input, long durationMs) {
        AgentResponse response = new AgentResponse();
        response.setAgentName(runtime.agentName());
        response.setSkill(runtime.skill());
        response.setBrainId(runtime.brain().getId());
        response.setProvider(runtime.provider());
        response.setModelName(runtime.modelName());
        response.setPrompt(prompt);
        response.setInput(input);
        response.setCorrelationType(correlationType(command));
        response.setCorrelationId(correlationId(command));
        response.setDurationMs((int) Math.min(Integer.MAX_VALUE, Math.max(0, durationMs)));
        return response;
    }

    private String correlationType(OrderExtractionCommand command) {
        if (command.draftOrderId() != null) {
            return "DRAFT_ORDER";
        }
        if (command.rawOrderTextId() != null) {
            return "RAW_ORDER_TEXT";
        }
        return "ORDER_EXTRACTION";
    }

    private UUID correlationId(OrderExtractionCommand command) {
        if (command.draftOrderId() != null) {
            return command.draftOrderId();
        }
        return command.rawOrderTextId();
    }
}
