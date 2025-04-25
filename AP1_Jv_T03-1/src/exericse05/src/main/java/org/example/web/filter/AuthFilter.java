package org.example.web.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.domain.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;
import java.util.UUID;

@Component
public class AuthFilter extends GenericFilterBean {
    private final AuthService authService;

    @Autowired
    public AuthFilter(AuthService authService) {
        this.authService = authService;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String path = httpRequest.getRequestURI();
        System.out.println("Request path: " + path); // Отладка
        System.out.println("Authorization header: " + httpRequest.getHeader("Authorization")); // Отладка

        if (path.equals("/auth/register") || path.equals("/auth/login")) {
            chain.doFilter(request, response);
            return;
        }

        String authHeader = httpRequest.getHeader("Authorization");
        UUID userId = authService.authorize(authHeader);

        if (userId != null) {
            httpRequest.setAttribute("userId", userId);
            chain.doFilter(request, response);
        } else {
            httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401, а не 403
            httpResponse.getWriter().write("Unauthorized");
        }
    }
}