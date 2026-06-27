package vn.ezisolutions.cloud.hackathon.modules.orderflow.ai.infrastructure;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import vn.ezisolutions.cloud.hackathon.core.exceptions.CustomValidationException;
import vn.ezisolutions.cloud.hackathon.modules.orderflow.ai.application.OrderFlowAgentRegistryService;
import vn.ezisolutions.cloud.hackathon.modules.orderflow.ai.application.OrderFlowAgentResponseLogService;
import vn.ezisolutions.cloud.hackathon.modules.orderflow.ai.application.OrderFlowAgentRuntime;
import vn.ezisolutions.cloud.hackathon.modules.orderflow.ai.domain.OrderExtractionCommand;
import vn.ezisolutions.cloud.hackathon.modules.orderflow.ai.domain.OrderExtractionGateway;
import vn.ezisolutions.cloud.hackathon.modules.orderflow.ai.domain.OrderExtractionResult;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class OpenAiOrderExtractionGateway implements OrderExtractionGateway {
    private final ObjectMapper mapper;
    private final OrderFlowAgentRegistryService agentRegistryService;
    private final OrderFlowAgentResponseLogService agentResponseLogService;

    @Value("${orderflow.openai.base-url:https://api.openai.com/v1}")
    private String baseUrl;

    @Override
    public OrderExtractionResult extract(OrderExtractionCommand command) {
        OrderFlowAgentRuntime runtime = agentRegistryService.resolveOpenAiRuntime(
                OrderFlowAgentRegistryService.ORDER_EXTRACTION_AGENT,
                OrderFlowAgentRegistryService.EXTRACT_SKILL
        );
        String prompt = userPrompt(runtime, command.rawText());
        Map<String, Object> request = buildRequest(runtime, prompt);
        long startedAt = System.nanoTime();
        try {
            Map<String, Object> response = RestClient.builder()
                    .baseUrl(baseUrl)
                    .defaultHeader("Authorization", "Bearer " + runtime.apiKey())
                    .build()
                    .post()
                    .uri("/responses")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(request)
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {
                    });
            String json = extractText(response);
            if (json == null || json.isBlank()) {
                throw new CustomValidationException("OpenAI không trả về JSON extraction", null);
            }
            Map<String, Object> parsed = mapper.readValue(json, new TypeReference<>() {
            });
            OrderExtractionResult result = toResult(parsed, response == null ? Map.of() : response);
            agentRegistryService.markSuccess(runtime.env().getId());
            logSuccess(runtime, command, prompt, parsed, response, json, elapsedMs(startedAt));
            return result;
        } catch (Exception exception) {
            agentRegistryService.markFailure(runtime.env().getId(), exception.getMessage());
            logFailure(runtime, command, prompt, exception, elapsedMs(startedAt));
            if (exception instanceof CustomValidationException validationException) {
                throw validationException;
            }
            throw new CustomValidationException("Không đọc được JSON extraction từ OpenAI: " + exception.getMessage(), null);
        }
    }

    private Map<String, Object> buildRequest(OrderFlowAgentRuntime runtime, String userPrompt) {
        Map<String, Object> request = new LinkedHashMap<>();
        request.put("model", runtime.modelName());
        request.put("input", List.of(
                Map.of("role", "system", "content", systemPrompt(runtime)),
                Map.of("role", "user", "content", userPrompt)
        ));
        if (runtime.brain().getTemperature() != null) {
            request.put("temperature", runtime.brain().getTemperature());
        }
        request.put("text", Map.of("format", schema()));
        return request;
    }

    private String systemPrompt(OrderFlowAgentRuntime runtime) {
        String systemInstruction = runtime.systemInstruction();
        if (systemInstruction.isBlank()) {
            throw new CustomValidationException("Order extraction brain chưa có system_instruction", null);
        }
        return systemInstruction;
    }

    private String userPrompt(OrderFlowAgentRuntime runtime, String rawText) {
        String promptTemplate = runtime.promptTemplate();
        if (promptTemplate.isBlank()) {
            return rawText;
        }
        return promptTemplate + "\n\nRAW_ORDER_TEXT:\n" + rawText;
    }

    private void logSuccess(OrderFlowAgentRuntime runtime, OrderExtractionCommand command, String prompt,
                            Map<String, Object> parsed, Map<String, Object> response, String outputText, long durationMs) {
        try {
            agentResponseLogService.success(
                    runtime,
                    command,
                    prompt,
                    safeInput(command),
                    Map.of("parsed", parsed, "openai", response == null ? Map.of() : response),
                    outputText,
                    durationMs
            );
        } catch (Exception ignored) {
            // Agent logging must not break order intake.
        }
    }

    private void logFailure(OrderFlowAgentRuntime runtime, OrderExtractionCommand command, String prompt,
                            Exception exception, long durationMs) {
        try {
            agentResponseLogService.failure(runtime, command, prompt, safeInput(command), exception.getMessage(), durationMs);
        } catch (Exception ignored) {
            // Agent logging must not hide the original OpenAI error.
        }
    }

    private Map<String, Object> safeInput(OrderExtractionCommand command) {
        Map<String, Object> input = new LinkedHashMap<>();
        input.put("organizationId", command.organizationId());
        input.put("customerId", command.customerId());
        input.put("projectId", command.projectId());
        input.put("draftOrderId", command.draftOrderId());
        input.put("rawOrderTextId", command.rawOrderTextId());
        input.put("rawText", command.rawText());
        return input;
    }

    private long elapsedMs(long startedAt) {
        return (System.nanoTime() - startedAt) / 1_000_000;
    }

    private Map<String, Object> schema() {
        Map<String, Object> attributeProperties = new LinkedHashMap<>();
        List<String> attributeNames = List.of(
                "productFamily",
                "material",
                "brand",
                "diameterMm",
                "nominalSize",
                "sizeSystem",
                "pressureClass",
                "thicknessMm",
                "fittingType",
                "angleDegree",
                "threadType",
                "reducerFromMm",
                "reducerToMm",
                "lengthM",
                "sellUnit"
        );
        attributeNames.forEach(name -> attributeProperties.put(
                name,
                Map.of("type", List.of("string", "null"))
        ));

        Map<String, Object> lineProperties = new LinkedHashMap<>();
        lineProperties.put("rawLineText", Map.of("type", "string"));
        lineProperties.put("itemDescription", Map.of("type", "string"));
        lineProperties.put("quantity", Map.of("type", "number"));
        lineProperties.put("requestedUnit", Map.of("type", "string"));
        lineProperties.put("extractedAttributes", Map.of(
                "type", "object",
                "additionalProperties", false,
                "properties", attributeProperties,
                "required", attributeNames
        ));
        lineProperties.put("confidenceScore", Map.of("type", "number", "minimum", 0, "maximum", 1));
        lineProperties.put("clarificationQuestion", Map.of("type", "string"));

        Map<String, Object> properties = new LinkedHashMap<>();
        properties.put("customerHint", Map.of("type", "string"));
        properties.put("projectHint", Map.of("type", "string"));
        properties.put("requestedDeliveryDate", Map.of("type", "string", "description", "ISO date yyyy-MM-dd or empty string"));
        properties.put("deliveryNote", Map.of("type", "string"));
        properties.put("clarificationQuestion", Map.of("type", "string"));
        properties.put("missingInformation", Map.of("type", "array", "items", Map.of("type", "string")));
        properties.put("lines", Map.of(
                "type", "array",
                "items", Map.of(
                        "type", "object",
                        "additionalProperties", false,
                        "properties", lineProperties,
                        "required", List.of("rawLineText", "itemDescription", "quantity", "requestedUnit", "extractedAttributes", "confidenceScore", "clarificationQuestion")
                )
        ));

        return Map.of(
                "type", "json_schema",
                "name", "orderflow_order_extraction",
                "strict", true,
                "schema", Map.of(
                        "type", "object",
                        "additionalProperties", false,
                        "properties", properties,
                        "required", List.of("customerHint", "projectHint", "requestedDeliveryDate", "deliveryNote", "clarificationQuestion", "missingInformation", "lines")
                )
        );
    }

    @SuppressWarnings("unchecked")
    private String extractText(Map<String, Object> response) {
        if (response == null) {
            return null;
        }
        Object outputText = response.get("output_text");
        if (outputText instanceof String text) {
            return text;
        }
        Object output = response.get("output");
        if (!(output instanceof List<?> outputList)) {
            return null;
        }
        for (Object item : outputList) {
            if (item instanceof Map<?, ?> itemMap && itemMap.get("content") instanceof List<?> contentList) {
                for (Object content : contentList) {
                    if (content instanceof Map<?, ?> contentMap && contentMap.get("text") instanceof String text) {
                        return text;
                    }
                }
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private OrderExtractionResult toResult(Map<String, Object> parsed, Map<String, Object> rawResponse) {
        List<OrderExtractionResult.ExtractedLine> lines = new ArrayList<>();
        Object rawLines = parsed.get("lines");
        if (rawLines instanceof List<?> lineList) {
            for (Object lineItem : lineList) {
                Map<String, Object> line = (Map<String, Object>) lineItem;
                lines.add(new OrderExtractionResult.ExtractedLine(
                        stringValue(line.get("rawLineText")),
                        stringValue(line.get("itemDescription")),
                        decimalValue(line.get("quantity")),
                        stringValue(line.get("requestedUnit")),
                        objectMap(line.get("extractedAttributes")),
                        decimalValue(line.get("confidenceScore")),
                        stringValue(line.get("clarificationQuestion"))
                ));
            }
        }
        return new OrderExtractionResult(
                stringValue(parsed.get("customerHint")),
                stringValue(parsed.get("projectHint")),
                dateValue(parsed.get("requestedDeliveryDate")),
                stringValue(parsed.get("deliveryNote")),
                stringValue(parsed.get("clarificationQuestion")),
                stringList(parsed.get("missingInformation")),
                lines,
                Map.of("parsed", parsed, "openai", rawResponse)
        );
    }

    private String stringValue(Object value) {
        return value == null ? "" : String.valueOf(value);
    }

    private BigDecimal decimalValue(Object value) {
        if (value == null || String.valueOf(value).isBlank()) {
            return BigDecimal.ZERO;
        }
        return new BigDecimal(String.valueOf(value));
    }

    private LocalDate dateValue(Object value) {
        String text = stringValue(value);
        return text.isBlank() ? null : LocalDate.parse(text);
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> objectMap(Object value) {
        return value instanceof Map<?, ?> map ? (Map<String, Object>) map : Map.of();
    }

    private List<String> stringList(Object value) {
        if (!(value instanceof List<?> list)) {
            return List.of();
        }
        return list.stream().map(String::valueOf).toList();
    }
}
