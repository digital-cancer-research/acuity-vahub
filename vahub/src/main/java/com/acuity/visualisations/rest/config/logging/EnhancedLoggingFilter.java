package com.acuity.visualisations.rest.config.logging;

import org.slf4j.MDC;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;
import java.util.Optional;

import static com.acuity.va.security.common.Constants.DEVELOPMENT_TEAM_AUTHORITY;

@Component
public class EnhancedLoggingFilter implements Filter {

    private static final String DEVELOPER_KEY = "developer";
    private static final String USERNAME_KEY = "username";

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws IOException, ServletException {
        Optional<Authentication> authentication = Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication());

        authentication.ifPresent(EnhancedLoggingFilter::register);

        chain.doFilter(servletRequest, servletResponse);

    }

    public static void register(Authentication auth) {
        auth.getAuthorities().stream()
                .filter(authority -> DEVELOPMENT_TEAM_AUTHORITY.equals(authority.getAuthority()))
                .findAny()
                .ifPresent(a -> registerDeveloperMode());
        auth.getAuthorities().stream()
                .filter(authority -> "REMOTE_ACUITY_USER".equals(authority.getAuthority()))
                .findAny()
                .ifPresent(a -> registerServiceMode());

        registerAuth(auth);
    }

    private static void registerServiceMode() {
        MDC.put(DEVELOPER_KEY, "service");
    }

    private static void registerAuth(Authentication auth) {
        MDC.put(USERNAME_KEY, auth.getName());
    }

    private static void registerDeveloperMode() {
        MDC.put(DEVELOPER_KEY, "dev");
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // default implementation ignored
    }

    @Override
    public void destroy() {
        // default implementation ignored
    }
}
