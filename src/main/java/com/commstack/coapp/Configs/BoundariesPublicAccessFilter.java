package com.commstack.coapp.Configs;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filter that ensures public access to the boundaries API.
 * This filter runs before the JWT filter to ensure boundaries endpoints
 * are always accessible without authentication.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class BoundariesPublicAccessFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(BoundariesPublicAccessFilter.class);

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        String requestUri = request.getRequestURI();
        log.info("BoundariesPublicAccessFilter processing: {}, method: {}", requestUri, request.getMethod());

        if (requestUri.startsWith("/api/boundaries")) {
            log.info("Public access granted for boundaries endpoint: {}", requestUri);

            // Ensure CORS headers for public endpoint
            response.setHeader("Access-Control-Allow-Origin", "*");
            response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
            response.setHeader("Access-Control-Allow-Headers", "Authorization, Content-Type");

            if (request.getMethod().equals("OPTIONS")) {
                response.setStatus(HttpServletResponse.SC_OK);
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}