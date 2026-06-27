package vn.ezisolutions.cloud.hackathon.core.utils;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import vn.ezisolutions.cloud.hackathon.core.common.AuthorizedUser;

import java.util.Optional;

public class SecurityUtils {
    public static Optional<AuthorizedUser> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof AuthorizedUser) {
            return Optional.of((AuthorizedUser) authentication.getPrincipal());
        }
        return Optional.empty();
    }

    public static boolean isAdmin(AuthorizedUser user) {
        return user != null && user.getRoles() != null && user.getRoles().contains("admin");
    }
}