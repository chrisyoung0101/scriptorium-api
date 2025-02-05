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

import java.util.Enumeration;

@Configuration
public class SecurityConfig {

    private static final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable()) // Disable CSRF (important for CORS)
                .cors(cors -> cors.configurationSource(request -> {
                    var corsConfig = new org.springframework.web.cors.CorsConfiguration();
                    corsConfig.addAllowedOrigin("https://scriptorium-ui.netlify.app"); // ✅ Allow frontend
                    corsConfig.addAllowedMethod("*"); // ✅ Allow all HTTP methods
                    corsConfig.addAllowedHeader("*"); // ✅ Allow all headers
                    corsConfig.setAllowCredentials(true);
                    return corsConfig;
                }))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()  // ✅ Allow all OPTIONS preflight requests
                        .anyRequest().authenticated()  // Other requests require authentication
                )
                .build();
    }


    @Bean
    public Filter logOptionsFilter() {
        return (request, response, chain) -> {
            HttpServletRequest httpRequest = (HttpServletRequest) request;
            HttpServletResponse httpResponse = (HttpServletResponse) response;

            // Log preflight request details
            if ("OPTIONS".equalsIgnoreCase(httpRequest.getMethod())) {
                logger.info("Received preflight request: Method = {}, URI = {}, Headers = {}",
                        httpRequest.getMethod(),
                        httpRequest.getRequestURI(),
                        extractHeaders(httpRequest));
            }

            chain.doFilter(request, response);

            // Ensure Vary: Origin is not duplicated
            if ("OPTIONS".equalsIgnoreCase(httpRequest.getMethod())) {
                preventDuplicateVaryHeader(httpResponse);
                logger.info("Responded to preflight request: Status = {}, Headers = {}",
                        httpResponse.getStatus(),
                        extractHeaders(httpResponse));
            }
        };
    }

    @Bean
    public Filter logAllRequestsFilter() {
        return (request, response, chain) -> {
            HttpServletRequest httpRequest = (HttpServletRequest) request;
            logger.debug("Received request: Method = {}, URI = {}, Headers = {}",
                    httpRequest.getMethod(),
                    httpRequest.getRequestURI(),
                    extractHeaders(httpRequest));

            chain.doFilter(request, response);
        };
    }

    private void preventDuplicateVaryHeader(HttpServletResponse response) {
        String varyHeader = response.getHeader("Vary");
        if (varyHeader == null || !varyHeader.contains("Origin")) {
            response.setHeader("Vary", "Origin");
        }
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
            headers.setLength(headers.length() - 2); // Remove trailing comma and space
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
            headers.setLength(headers.length() - 2); // Remove trailing comma and space
        }
        return headers.toString();
    }
}
