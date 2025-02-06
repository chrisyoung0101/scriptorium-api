package com.example.scriptorium_api.config;

import jakarta.servlet.Filter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.CorsFilter;

import java.util.Enumeration;

@Configuration
public class SecurityConfig {

    private static final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, CorsFilter corsFilter) throws Exception {
        return http
                .csrf(csrf -> csrf.disable()) // Disable CSRF
                .addFilterBefore(corsFilter, UsernamePasswordAuthenticationFilter.class) // ✅ Ensure CORS filter runs before authentication
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()  // ✅ Allow all OPTIONS requests explicitly
                        .anyRequest().authenticated()  // Secure other requests
                )
                .build();
    }

    @Bean
    public Filter logOptionsFilter() {
        return (request, response, chain) -> {
            HttpServletRequest httpRequest = (HttpServletRequest) request;
            HttpServletResponse httpResponse = (HttpServletResponse) response;

            if ("OPTIONS".equalsIgnoreCase(httpRequest.getMethod())) {
                logger.info("🟡 Preflight Request Received → Method: {}, URI: {}, Headers: {}",
                        httpRequest.getMethod(),
                        httpRequest.getRequestURI(),
                        extractHeaders(httpRequest));
            }

            chain.doFilter(request, response);

            if ("OPTIONS".equalsIgnoreCase(httpRequest.getMethod())) {
                logger.info("🟢 Preflight Response Sent → Status: {}, Headers: {}",
                        httpResponse.getStatus(),
                        extractHeaders(httpResponse));
            }
        };
    }

    private String extractHeaders(HttpServletRequest request) {
        StringBuilder headers = new StringBuilder();
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            headers.append(headerName)
                    .append(": ")
                    .append(request.getHeader(headerName))
                    .append(", ");
        }
        if (headers.length() > 0) {
            headers.setLength(headers.length() - 2);
        }
        return headers.toString();
    }

    private String extractHeaders(HttpServletResponse response) {
        StringBuilder headers = new StringBuilder();
        for (String headerName : response.getHeaderNames()) {
            headers.append(headerName)
                    .append(": ")
                    .append(response.getHeader(headerName))
                    .append(", ");
        }
        if (headers.length() > 0) {
            headers.setLength(headers.length() - 2);
        }
        return headers.toString();
    }
}
