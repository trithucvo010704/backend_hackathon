package vn.ezisolutions.cloud.hackathon.core.filters;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import vn.ezisolutions.cloud.hackathon.core.common.AuthorizedUser;
import vn.ezisolutions.cloud.hackathon.services.auth.AuthService;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class AppTokenFilter extends OncePerRequestFilter {
    private static final Logger logger = LoggerFactory.getLogger(AppTokenFilter.class);

    private final AuthService authService;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }
        String path = request.getServletPath();
        return !path.startsWith("/app/") || path.startsWith("/app/auth");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String jwt = request.getHeader("Authorization");
        if (jwt == null || !isValidToken(jwt)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Unauthenticated");
            return;
        }
        filterChain.doFilter(request, response);
    }

    private boolean isValidToken(String token) {
        if (!token.startsWith("Bearer ")) {
            return false;
        }
        token = token.replaceFirst("Bearer ", "");
        try {
            AuthorizedUser user = authService.getUserByToken(token);
            if (user == null) {
                throw new Exception(String.format("Token %s** not found", token.substring(0, 5)));
            }
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
            return true;
        } catch (Exception e) {
            logger.error(e.getMessage());
            return false;
        }
    }
}
