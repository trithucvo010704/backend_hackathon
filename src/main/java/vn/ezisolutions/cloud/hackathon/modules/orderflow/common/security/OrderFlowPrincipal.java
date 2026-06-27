package vn.ezisolutions.cloud.hackathon.modules.orderflow.common.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public record OrderFlowPrincipal(
        UUID userId,
        UUID organizationId,
        String email,
        String displayName,
        String role
) {
    public Collection<? extends GrantedAuthority> authorities() {
        return List.of(new SimpleGrantedAuthority(role));
    }
}
