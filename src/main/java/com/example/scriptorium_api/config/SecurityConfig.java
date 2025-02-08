package com.example.scriptorium_api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.CorsFilter;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, CorsFilter corsFilter) throws Exception {
        return http
                .csrf(csrf -> csrf.disable()) // Disable CSRF for APIs
                .addFilterBefore(corsFilter, UsernamePasswordAuthenticationFilter.class) // Ensure CORS runs before authentication
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll() // ✅ Allow OPTIONS requests
                        .requestMatchers("/actuator/health").permitAll() // ✅ Allow public health check
                        .requestMatchers("/api/documents").authenticated() // 🔒 Require authentication for /api/documents
                        .anyRequest().authenticated() // 🔒 Require authentication for all other requests
                )
                .httpBasic(withDefaults()) // ✅ Enable basic auth for easy testing
                .build();
    }


    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails user = User.builder()
                .username("admin")
                .password("{noop}admin123") // 🔒 In-memory user for testing (replace with real auth)
                .roles("USER")
                .build();
        return new InMemoryUserDetailsManager(user);
    }
}
