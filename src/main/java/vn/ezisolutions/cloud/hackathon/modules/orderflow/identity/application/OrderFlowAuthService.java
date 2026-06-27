package vn.ezisolutions.cloud.hackathon.modules.orderflow.identity.application;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.ezisolutions.cloud.hackathon.core.exceptions.CustomValidationException;
import vn.ezisolutions.cloud.hackathon.modules.orderflow.common.security.OrderFlowJwtService;
import vn.ezisolutions.cloud.hackathon.modules.orderflow.common.security.OrderFlowPrincipal;
import vn.ezisolutions.cloud.hackathon.modules.orderflow.common.domain.AppUserEntity;
import vn.ezisolutions.cloud.hackathon.modules.orderflow.common.domain.OrganizationEntity;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class OrderFlowAuthService {
    private final EntityManager entityManager;
    private final OrderFlowJwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public Map<String, Object> login(String email, String password) {
        AppUserEntity user = entityManager.createQuery("""
                        SELECT u FROM AppUserEntity u
                        WHERE lower(u.email) = lower(:email)
                        """, AppUserEntity.class)
                .setParameter("email", email == null ? "" : email.trim())
                .getResultStream()
                .findFirst()
                .orElseThrow(() -> new CustomValidationException("Email hoặc mật khẩu không đúng", null));
        if (!Boolean.TRUE.equals(user.getActive()) || user.getPasswordHash() == null
                || !passwordEncoder.matches(password == null ? "" : password, user.getPasswordHash())) {
            throw new CustomValidationException("Email hoặc mật khẩu không đúng", null);
        }
        OrderFlowPrincipal principal = toPrincipal(user);
        String token = jwtService.issue(principal);
        return Map.of(
                "accessToken", token,
                "tokenType", "Bearer",
                "user", currentUserPayload(principal)
        );
    }

    @Transactional(readOnly = true)
    public Map<String, Object> currentUserPayload(OrderFlowPrincipal principal) {
        OrganizationEntity organization = entityManager.find(OrganizationEntity.class, principal.organizationId());
        return Map.of(
                "id", principal.userId(),
                "organizationId", principal.organizationId(),
                "organizationName", organization == null ? "" : organization.getName(),
                "email", principal.email(),
                "displayName", principal.displayName(),
                "role", principal.role()
        );
    }

    private OrderFlowPrincipal toPrincipal(AppUserEntity user) {
        return new OrderFlowPrincipal(
                user.getId(),
                user.getOrganizationId(),
                user.getEmail(),
                user.getDisplayName(),
                user.getRole().name()
        );
    }
}
