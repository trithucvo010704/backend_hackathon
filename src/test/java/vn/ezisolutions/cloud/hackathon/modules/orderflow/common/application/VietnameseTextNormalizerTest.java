package vn.ezisolutions.cloud.hackathon.modules.orderflow.common.application;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class VietnameseTextNormalizerTest {
    private final VietnameseTextNormalizer normalizer = new VietnameseTextNormalizer();

    @Test
    void normalizesVietnamesePlumbingText() {
        String result = normalizer.normalize("Ống nóng D25, co 90 phi 27 Bình Minh");

        assertThat(result).contains("ong nong");
        assertThat(result).contains("phi 25");
        assertThat(result).contains("co 90 phi 27");
        assertThat(result).contains("binh minh");
    }
}
