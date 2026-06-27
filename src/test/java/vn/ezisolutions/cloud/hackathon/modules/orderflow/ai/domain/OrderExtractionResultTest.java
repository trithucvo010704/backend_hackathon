package vn.ezisolutions.cloud.hackathon.modules.orderflow.ai.domain;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class OrderExtractionResultTest {
    @Test
    void exposesRawAliasForLegacyWorkflowCompatibility() {
        Map<String, Object> raw = Map.of("provider", "openai", "lineCount", 1);
        OrderExtractionResult result = new OrderExtractionResult(
                "Minh Anh",
                "Quận 7",
                LocalDate.of(2026, 6, 28),
                "Giao giờ hành chính",
                "",
                List.of(),
                List.of(new OrderExtractionResult.ExtractedLine(
                        "10 cây ống PVC phi 21 Bình Minh",
                        "Ống PVC phi 21 Bình Minh",
                        BigDecimal.TEN,
                        "cây",
                        Map.of("material", "PVC", "diameterMm", 21, "brand", "Bình Minh"),
                        new BigDecimal("0.90"),
                        ""
                )),
                raw
        );

        assertThat(result.raw()).isSameAs(raw);
        assertThat(result.lines()).hasSize(1);
        assertThat(result.lines().getFirst().quantity()).isEqualByComparingTo("10");
    }
}
