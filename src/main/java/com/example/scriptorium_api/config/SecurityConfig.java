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

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, CorsFilter corsFilter) throws Exception {
        return http
                .csrf(csrf -> csrf.disable()) // ✅ Disable CSRF (good for APIs)
                .addFilterBefore(corsFilter, UsernamePasswordAuthenticationFilter.class) // ✅ Ensure CORS filter runs first
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()  // ✅ Allow all OPTIONS requests (CORS preflight)
                        .requestMatchers("/actuator/health").permitAll()  // ✅ Allow health checks without auth
                        .requestMatchers("/api/public/**").permitAll()  // ✅ Allow public API endpoints
                        .anyRequest().authenticated()  // 🔒 Require authentication for all other requests
                )
                .httpBasic(httpBasic -> httpBasic.disable()) // 🔒 Disabling HTTP Basic (use JWT or Session)
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
