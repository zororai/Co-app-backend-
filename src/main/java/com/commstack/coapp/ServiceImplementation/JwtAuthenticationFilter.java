package com.commstack.coapp.ServiceImplementation;

import com.commstack.coapp.Repositories.UserService;
import com.commstack.coapp.Service.JwtService;
import io.jsonwebtoken.io.IOException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserService userService;

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    @Override
    protected boolean shouldNotFilter(@NonNull HttpServletRequest request) {
        String requestURI = request.getRequestURI();

        // Skip JWT filter for public endpoints
        boolean shouldSkip = requestURI.startsWith("/api/boundaries") ||
                requestURI.startsWith("/api/area-names");

        if (shouldSkip) {
            log.info("Skipping JWT filter for public endpoint: {}", requestURI);
        }
        return shouldSkip;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response, @NonNull FilterChain filterChain)
            throws ServletException, IOException, java.io.IOException {
        final String authHeader = request.getHeader("Authorization");
        final String requestURI = request.getRequestURI();

        log.info("JWT filter processing: URI={}, Method={}, HasAuthHeader={}",
                requestURI, request.getMethod(), (authHeader != null));

        // Double check for public endpoints
        if (requestURI.startsWith("/api/boundaries") || requestURI.startsWith("/api/area-names")) {
            log.info("Public endpoint detected, skipping JWT authentication: {}", requestURI);
            filterChain.doFilter(request, response);
            return;
        }

        // If no auth header or not Bearer token, continue filter chain
        if (StringUtils.isEmpty(authHeader) || !StringUtils.startsWith(authHeader, "Bearer ")) {
            log.info("No valid Authorization header found, skipping JWT validation");
            filterChain.doFilter(request, response);
            return;
        }

        // We can safely use substring here as we've verified authHeader is not null and
        // starts with "Bearer "
        final String jwt = authHeader.substring(7);
        final String userEmail = jwtService.extractUserName(jwt);
        if (StringUtils.isNotEmpty(userEmail)
                && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userService.userDetailsService()
                    .loadUserByUsername(userEmail);

            if (jwtService.isTokenValid(jwt, userDetails)) {
                SecurityContext context = SecurityContextHolder.createEmptyContext();
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                context.setAuthentication(authToken);
                SecurityContextHolder.setContext(context);
            }
        }
        filterChain.doFilter(request, response);
    }
}
