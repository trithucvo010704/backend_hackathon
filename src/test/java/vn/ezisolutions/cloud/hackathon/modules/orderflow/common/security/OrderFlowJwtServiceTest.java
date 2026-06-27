package vn.ezisolutions.cloud.hackathon.modules.orderflow.common.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class OrderFlowJwtServiceTest {
    private final OrderFlowJwtService service = new OrderFlowJwtService(new ObjectMapper(), "unit-test-secret", 3600);

    @Test
    void issuesAndParsesToken() {
        UUID userId = UUID.randomUUID();
        UUID organizationId = UUID.randomUUID();
        OrderFlowPrincipal principal = new OrderFlowPrincipal(userId, organizationId, "sale.admin@orderflow.local", "Sale Admin", "SALE_ADMIN");

        String token = service.issue(principal);
        OrderFlowPrincipal parsed = service.parse(token);

        assertThat(parsed).isNotNull();
        assertThat(parsed.userId()).isEqualTo(userId);
        assertThat(parsed.organizationId()).isEqualTo(organizationId);
        assertThat(parsed.role()).isEqualTo("SALE_ADMIN");
    }

    @Test
    void rejectsTamperedToken() {
        OrderFlowPrincipal principal = new OrderFlowPrincipal(UUID.randomUUID(), UUID.randomUUID(), "a@b.com", "A", "SALE_ADMIN");
        String token = service.issue(principal);

        assertThat(service.parse(token + "x")).isNull();
    }
}
