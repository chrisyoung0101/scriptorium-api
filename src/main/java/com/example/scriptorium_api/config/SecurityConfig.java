package com.example.scriptorium_api.config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;


import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.web.SecurityFilterChain;

import java.io.IOException;

@Configuration
public class SecurityConfig {

    private static final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(request -> {
                    var corsConfig = new org.springframework.web.cors.CorsConfiguration();
                    corsConfig.addAllowedOrigin("https://scriptorium-ui.netlify.app");
                    corsConfig.addAllowedMethod("*");
                    corsConfig.addAllowedHeader("*");
                    corsConfig.setAllowCredentials(true);
                    return corsConfig;
                }))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(logOptionsFilter(), org.springframework.web.filter.CorsFilter.class)
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
                        httpRequest.getHeaderNames());
            }

            chain.doFilter(request, response);

            // Log the response for preflight
            if ("OPTIONS".equalsIgnoreCase(httpRequest.getMethod())) {
                logger.info("Responded to preflight request: Status = {}, Headers = {}",
                        httpResponse.getStatus(),
                        httpResponse.getHeaderNames());
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
                    httpRequest.getHeaderNames());

            chain.doFilter(request, response);
        };
    }




}

