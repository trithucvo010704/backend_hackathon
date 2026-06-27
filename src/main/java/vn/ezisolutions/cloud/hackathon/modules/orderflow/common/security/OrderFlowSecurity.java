package vn.ezisolutions.cloud.hackathon.modules.orderflow.common.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public final class OrderFlowSecurity {
    private OrderFlowSecurity() {
    }

    public static OrderFlowPrincipal currentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof OrderFlowPrincipal principal)) {
            throw new IllegalStateException("Missing OrderFlow user");
        }
        return principal;
    }
}
