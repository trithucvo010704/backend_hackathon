package vn.ezisolutions.cloud.hackathon.modules.orderflow.ai.domain;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public record OrderExtractionResult(
        String customerHint,
        String projectHint,
        LocalDate requestedDeliveryDate,
        String deliveryNote,
        String clarificationQuestion,
        List<String> missingInformation,
        List<ExtractedLine> lines,
        Map<String, Object> rawResult
) {
    public Map<String, Object> raw() {
        return rawResult;
    }

    public record ExtractedLine(
            String rawLineText,
            String itemDescription,
            BigDecimal quantity,
            String requestedUnit,
            Map<String, Object> extractedAttributes,
            BigDecimal confidenceScore,
            String clarificationQuestion
    ) {
    }
}
