package com.example.scriptorium_api.config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;



@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorize -> authorize
//                        .requestMatchers("/api/**").authenticated()
                        .requestMatchers("/api/**").permitAll() // Allow unauthenticated access to /api/*
                        .anyRequest().permitAll()
                )
                .httpBasic(httpBasic -> httpBasic.disable()) // Customize the behavior here
                .csrf(csrf -> csrf.disable()); // Disable CSRF for simplicity (not recommended for production)

        return http.build();
    }
}
