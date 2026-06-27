package vn.ezisolutions.cloud.hackathon.modules.orderflow.common.application;

import org.springframework.stereotype.Component;

import java.text.Normalizer;
import java.util.Locale;

@Component
public class VietnameseTextNormalizer {
    public String normalize(String input) {
        if (input == null) {
            return "";
        }
        String value = input.toLowerCase(Locale.ROOT)
                .replace("ø", " phi ")
                .replace("đ", "d")
                .replaceAll("[,;:\\n\\r\\t]+", " ")
                .replaceAll("\\bd\\s*(\\d+)", " phi $1")
                .replaceAll("\\s+", " ")
                .trim();
        return stripAccents(value);
    }

    private String stripAccents(String value) {
        String normalized = Normalizer.normalize(value, Normalizer.Form.NFD);
        return normalized.replaceAll("\\p{M}", "");
    }
}
