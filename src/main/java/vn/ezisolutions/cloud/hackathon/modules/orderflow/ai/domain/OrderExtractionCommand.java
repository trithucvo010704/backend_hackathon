package vn.ezisolutions.cloud.hackathon.modules.orderflow.ai.domain;

import java.util.UUID;

public record OrderExtractionCommand(
        UUID organizationId,
        UUID customerId,
        UUID projectId,
        UUID draftOrderId,
        UUID rawOrderTextId,
        String rawText
) {
    public OrderExtractionCommand(UUID organizationId, UUID customerId, UUID projectId, String rawText) {
        this(organizationId, customerId, projectId, null, null, rawText);
    }
}
