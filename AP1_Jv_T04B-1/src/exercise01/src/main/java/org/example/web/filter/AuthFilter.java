package org.example.web.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.domain.service.AuthorisationService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;
import java.util.UUID;

public class AuthFilter extends GenericFilterBean {
    private final AuthorisationService authService;

    public AuthFilter(AuthorisationService authService) {
        this.authService = authService;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        String path = httpRequest.getRequestURI();
        String method = httpRequest.getMethod();

        System.out.println("Filter: Path = " + path + ", Method = " + method);

        if (path.endsWith("/auth/signup") || path.endsWith("/auth/login")) {
            System.out.println("Filter: Skipping auth check for " + path);
            filterChain.doFilter(request, response);
            return;
        }

        if ("OPTIONS".equalsIgnoreCase(method)) {
            httpResponse.setHeader("Access-Control-Allow-Origin", "http://localhost:63342");
            httpResponse.setHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
            httpResponse.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
            httpResponse.setHeader("Access-Control-Max-Age", "3600");
            httpResponse.setStatus(HttpServletResponse.SC_OK);
            System.out.println("Filter: Handling OPTIONS request");
            return;
        }

        String authHeader = httpRequest.getHeader("Authorization");
        if (authHeader == null || authHeader.isEmpty()) {
            httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            httpResponse.setContentType("application/json");
            httpResponse.getWriter().write("{\"error\": \"Missing Authorization header\"}");
            System.out.println("Filter: Missing Authorization header");
            return;
        }

        UUID userId = authService.authorise(authHeader);
        if (userId != null) {
            // Устанавливаем Authentication в SecurityContext
            Authentication auth = new UsernamePasswordAuthenticationToken(userId.toString(), null, null);
            SecurityContextHolder.getContext().setAuthentication(auth);
            httpRequest.setAttribute("userId", userId);
            filterChain.doFilter(request, response);
        } else {
            httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            httpResponse.setContentType("application/json");
            httpResponse.getWriter().write("{\"error\": \"Invalid credentials\"}");
            System.out.println("Filter: Invalid credentials");
        }
    }
}