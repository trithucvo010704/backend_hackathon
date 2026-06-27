package vn.ezisolutions.cloud.hackathon.modules.orderflow.common.security;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class OrderFlowJwtService {
    private final ObjectMapper mapper;
    private final byte[] secret;
    private final long ttlSeconds;

    public OrderFlowJwtService(
            ObjectMapper mapper,
            @Value("${orderflow.security.jwt-secret}") String secret,
            @Value("${orderflow.security.token-ttl-seconds:86400}") long ttlSeconds) {
        this.mapper = mapper;
        this.secret = secret.getBytes(StandardCharsets.UTF_8);
        this.ttlSeconds = ttlSeconds;
    }

    public String issue(OrderFlowPrincipal principal) {
        try {
            Map<String, Object> header = Map.of("alg", "HS256", "typ", "JWT");
            Map<String, Object> payload = new LinkedHashMap<>();
            payload.put("sub", principal.userId().toString());
            payload.put("organizationId", principal.organizationId().toString());
            payload.put("email", principal.email());
            payload.put("displayName", principal.displayName());
            payload.put("role", principal.role());
            payload.put("exp", Instant.now().plusSeconds(ttlSeconds).getEpochSecond());
            String headerPart = encode(mapper.writeValueAsBytes(header));
            String payloadPart = encode(mapper.writeValueAsBytes(payload));
            String unsigned = headerPart + "." + payloadPart;
            return unsigned + "." + encode(hmac(unsigned));
        } catch (Exception exception) {
            throw new IllegalStateException("Cannot issue OrderFlow token", exception);
        }
    }

    public OrderFlowPrincipal parse(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length != 3) {
                return null;
            }
            String unsigned = parts[0] + "." + parts[1];
            String expected = encode(hmac(unsigned));
            if (!constantTimeEquals(expected, parts[2])) {
                return null;
            }
            Map<String, Object> payload = mapper.readValue(Base64.getUrlDecoder().decode(parts[1]), new TypeReference<>() {
            });
            Number exp = (Number) payload.get("exp");
            if (exp == null || exp.longValue() < Instant.now().getEpochSecond()) {
                return null;
            }
            return new OrderFlowPrincipal(
                    UUID.fromString(String.valueOf(payload.get("sub"))),
                    UUID.fromString(String.valueOf(payload.get("organizationId"))),
                    String.valueOf(payload.get("email")),
                    String.valueOf(payload.get("displayName")),
                    String.valueOf(payload.get("role"))
            );
        } catch (Exception exception) {
            return null;
        }
    }

    private String encode(byte[] bytes) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private byte[] hmac(String unsigned) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(secret, "HmacSHA256"));
        return mac.doFinal(unsigned.getBytes(StandardCharsets.UTF_8));
    }

    private boolean constantTimeEquals(String left, String right) {
        if (left.length() != right.length()) {
            return false;
        }
        int result = 0;
        for (int i = 0; i < left.length(); i++) {
            result |= left.charAt(i) ^ right.charAt(i);
        }
        return result == 0;
    }
}
