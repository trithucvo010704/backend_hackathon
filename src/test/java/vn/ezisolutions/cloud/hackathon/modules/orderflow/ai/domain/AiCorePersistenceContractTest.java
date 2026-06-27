package vn.ezisolutions.cloud.hackathon.modules.orderflow.ai.domain;

import jakarta.persistence.Lob;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import vn.ezisolutions.cloud.hackathon.documents.ai_core.AgentBrain;
import vn.ezisolutions.cloud.hackathon.documents.ai_core.AgentResponse;
import vn.ezisolutions.cloud.hackathon.documents.ai_core.AiEnv;
import vn.ezisolutions.cloud.hackathon.documents.ai_core.AssistantAgent;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AiCorePersistenceContractTest {
    private static final Pattern SALE_ADMIN_PASSWORD_HASH = Pattern.compile(
            "'sale\\.admin@orderflow\\.local',\\s*'([^']+)'"
    );

    @Test
    void demoPasswordMatchesDocumentedLocalCredential() throws IOException {
        String seedSql;
        try (var input = getClass().getResourceAsStream(
                "/db/migration/V2__orderflow_seed_demo.sql"
        )) {
            assertNotNull(input);
            seedSql = new String(input.readAllBytes(), StandardCharsets.UTF_8);
        }

        Matcher matcher = SALE_ADMIN_PASSWORD_HASH.matcher(seedSql);
        assertTrue(matcher.find());
        assertTrue(new BCryptPasswordEncoder().matches("password", matcher.group(1)));
    }

    @Test
    void postgresTextColumnsAreNotMappedAsLobs() throws NoSuchFieldException {
        List<Field> textFields = List.of(
                AssistantAgent.class.getDeclaredField("skills"),
                AiEnv.class.getDeclaredField("serviceAccountJson"),
                AgentBrain.class.getDeclaredField("systemInstruction"),
                AgentBrain.class.getDeclaredField("promptTemplate"),
                AgentResponse.class.getDeclaredField("prompt"),
                AgentResponse.class.getDeclaredField("input"),
                AgentResponse.class.getDeclaredField("output"),
                AgentResponse.class.getDeclaredField("outputText"),
                AgentResponse.class.getDeclaredField("tools")
        );

        textFields.forEach(field -> assertFalse(
                field.isAnnotationPresent(Lob.class),
                () -> field.getDeclaringClass().getSimpleName() + "." + field.getName()
                        + " must map PostgreSQL TEXT directly"
        ));
    }
}
