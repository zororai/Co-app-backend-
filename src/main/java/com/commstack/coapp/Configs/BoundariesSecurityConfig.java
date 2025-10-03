package com.commstack.coapp.Configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import static org.springframework.security.config.Customizer.withDefaults;

/**
 * Special security configuration that specifically allows public access to
 * boundaries endpoints.
 * This configuration will run before the main security configuration.
 */
@Configuration
@EnableWebSecurity
public class BoundariesSecurityConfig {

    @Bean
    @Order(1) // Run this before the main security config
    public SecurityFilterChain boundariesFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatchers((matchers) -> matchers
                        .requestMatchers("/api/boundaries/**")
                        .requestMatchers("/api/area-names/**"))
                .authorizeHttpRequests(authorize -> authorize
                        .anyRequest().permitAll())
                .cors(withDefaults())
                .csrf(csrf -> csrf.disable());

        return http.build();
    }
}