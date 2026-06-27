package vn.ezisolutions.cloud.hackathon.services.ai_core;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.genai.Client;
import com.google.genai.types.ClientOptions;
import com.google.genai.types.HttpOptions;
import com.google.genai.types.HttpRetryOptions;
import io.modelcontextprotocol.client.McpClient;
import io.modelcontextprotocol.client.McpSyncClient;
import io.modelcontextprotocol.client.transport.HttpClientStreamableHttpTransport;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.google.genai.GoogleGenAiChatModel;
import org.springframework.ai.google.genai.GoogleGenAiChatOptions;
import org.springframework.ai.google.genai.common.GoogleGenAiThinkingLevel;
import org.springframework.ai.mcp.SyncMcpToolCallbackProvider;
import org.springframework.stereotype.Service;
import vn.ezisolutions.cloud.hackathon.documents.ai_core.AgentBrain;
import vn.ezisolutions.cloud.hackathon.documents.ai_core.AgentResponse;
import vn.ezisolutions.cloud.hackathon.documents.ai_core.AiEnv;
import vn.ezisolutions.cloud.hackathon.documents.ai_core.AiModel;
import vn.ezisolutions.cloud.hackathon.documents.ai_core.AssistantAgent;
import vn.ezisolutions.cloud.hackathon.documents.ai_core.McpServer;
import vn.ezisolutions.cloud.hackathon.repositories.ai_core.AgentBrainRepository;
import vn.ezisolutions.cloud.hackathon.repositories.ai_core.AiModelRepository;
import vn.ezisolutions.cloud.hackathon.repositories.ai_core.AssistantAgentRepository;
import vn.ezisolutions.cloud.hackathon.repositories.ai_core.McpServerRepository;

import java.io.ByteArrayInputStream;
import java.net.http.HttpRequest;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AgentService {
    private final AssistantAgentRepository assistantAgentRepository;
    private final AgentBrainRepository agentBrainRepository;
    private final AiModelRepository aiModelRepository;
    private final McpServerRepository mcpServerRepository;
    private final ObjectMapper mapper;

    public AssistantAgent findAgent(String agentName) {
        return assistantAgentRepository.findFirstByName(agentName)
                .orElseThrow(() -> new IllegalStateException("Not found assistant agent: " + agentName));
    }

    public AgentBrain fetchBrain(AssistantAgent agent, String skill) {
        String brainId = agent.getSkills() == null ? null : agent.getSkills().get(skill);
        if (brainId == null || brainId.isBlank()) {
            throw new IllegalStateException("Not found agent skill: " + skill);
        }
        AgentBrain brain = agentBrainRepository.findById(UUID.fromString(brainId))
                .orElseThrow(() -> new IllegalStateException("Not found agent brain: " + brainId));
        AiModel model = aiModelRepository.findById(brain.getModelId())
                .orElseThrow(() -> new IllegalStateException("Not found AI model: " + brain.getModelId()));
        brain.setModel(model);
        loadMcpServers(brain);
        return brain;
    }

    public ChatClient createAgentClient(AgentBrain brain, AiEnv env) {
        return createAgentClient(brain, env, null);
    }

    public ChatClient createAgentClient(AgentBrain brain, AiEnv env, Map<String, String> extraMcpHeaders) {
        if (brain.getModel() == null) {
            throw new IllegalStateException("Agent brain model is required");
        }
        if (!"GEMINI".equalsIgnoreCase(brain.getModel().getProvider())) {
            throw new IllegalStateException("Unsupported AI provider: " + brain.getModel().getProvider());
        }
        try {
            Client.Builder clientBuilder = buildGoogleClient(env);
            GoogleGenAiChatOptions.Builder optionsBuilder = GoogleGenAiChatOptions.builder()
                    .model(brain.getModel().getName())
                    .temperature(brain.getTemperature());

            if (Boolean.TRUE.equals(brain.getGoogleSearch())) {
                optionsBuilder.googleSearchRetrieval(true);
            }
            if (brain.getIncludeThoughts() != null) {
                optionsBuilder.includeThoughts(brain.getIncludeThoughts());
            }
            if ("LOW".equalsIgnoreCase(brain.getThinkingLevel())) {
                optionsBuilder.thinkingLevel(GoogleGenAiThinkingLevel.LOW);
            } else if ("HIGH".equalsIgnoreCase(brain.getThinkingLevel())) {
                optionsBuilder.thinkingLevel(GoogleGenAiThinkingLevel.HIGH);
            }

            ChatModel chatModel = GoogleGenAiChatModel.builder()
                    .genAiClient(clientBuilder.build())
                    .defaultOptions(optionsBuilder.build())
                    .build();
            ChatClient.Builder chatClientBuilder = ChatClient.builder(chatModel)
                    .defaultSystem(brain.getSystemInstruction() == null ? "" : brain.getSystemInstruction());
            if (brain.getMcpServers() != null && !brain.getMcpServers().isEmpty()) {
                List<McpSyncClient> mcpClients = brain.getMcpServers().stream()
                        .map(mcpServer -> createMcpClient(mcpServer, extraMcpHeaders))
                        .toList();
                SyncMcpToolCallbackProvider provider = SyncMcpToolCallbackProvider.builder()
                        .mcpClients(mcpClients)
                        .build();
                chatClientBuilder.defaultToolCallbacks(provider);
            }
            return chatClientBuilder.build();
        } catch (Exception exception) {
            throw new IllegalStateException("Lỗi cấu hình AI Model", exception);
        }
    }

    public void logAgentResponse(AgentResponse agentResponse, ChatResponse chatResponse) {
        if (chatResponse == null || chatResponse.getResult() == null) {
            return;
        }
        Generation generation = chatResponse.getResult();
        if (generation.getMetadata() != null) {
            agentResponse.setFinishReason(generation.getMetadata().getFinishReason());
        }
        if (generation.getOutput() == null) {
            return;
        }
        agentResponse.setOutputText(generation.getOutput().getText());
        if (generation.getOutput().hasToolCalls()) {
            try {
                agentResponse.setTools(mapper.writeValueAsString(generation.getOutput().getToolCalls()));
            } catch (Exception exception) {
                agentResponse.setTools(String.valueOf(generation.getOutput().getToolCalls()));
            }
        }
    }

    private void loadMcpServers(AgentBrain brain) {
        if (brain.getMcpServerIds() == null || brain.getMcpServerIds().isBlank()) {
            return;
        }
        List<McpServer> mcpServers = new ArrayList<>();
        for (String rawId : brain.getMcpServerIds().split(",")) {
            String id = rawId == null ? "" : rawId.trim();
            if (id.isBlank()) {
                continue;
            }
            mcpServerRepository.findById(UUID.fromString(id)).ifPresent(mcpServers::add);
        }
        brain.setMcpServers(mcpServers);
    }

    private McpSyncClient createMcpClient(McpServer mcpServer, Map<String, String> extraHeaders) {
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .setHeader("X-TOKEN", mcpServer.getId().toString());
        if (extraHeaders != null) {
            extraHeaders.forEach(requestBuilder::header);
        }

        var transport = HttpClientStreamableHttpTransport.builder(mcpServer.getUrl())
                .requestBuilder(requestBuilder)
                .endpoint(mcpServer.getSseEndpoint())
                .build();
        McpSyncClient mcpClient = McpClient.sync(transport)
                .requestTimeout(Duration.of(60, ChronoUnit.SECONDS))
                .build();
        mcpClient.initialize();
        return mcpClient;
    }

    private Client.Builder buildGoogleClient(AiEnv env) throws Exception {
        Client.Builder builder = Client.builder();
        if (Boolean.TRUE.equals(env.getProductionMode())) {
            builder.vertexAI(true)
                    .location(env.getLocation())
                    .project(env.getProjectId());
            if (env.getServiceAccountJson() != null && !env.getServiceAccountJson().isBlank()) {
                GoogleCredentials credentials = GoogleCredentials.fromStream(
                        new ByteArrayInputStream(env.getServiceAccountJson().getBytes(StandardCharsets.UTF_8))
                );
                builder.credentials(credentials);
            }
        } else {
            builder.vertexAI(false).apiKey(env.getApiKey());
        }
        return builder.clientOptions(ClientOptions.builder()
                        .maxConnections(64)
                        .maxConnectionsPerHost(16)
                        .build())
                .httpOptions(HttpOptions.builder()
                        .timeout(60_000)
                        .retryOptions(HttpRetryOptions.builder().attempts(3).httpStatusCodes(408, 429).build())
                        .build());
    }
}
