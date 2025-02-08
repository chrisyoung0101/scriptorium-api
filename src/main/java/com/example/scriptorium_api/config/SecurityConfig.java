package com.example.scriptorium_api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Disable CSRF as it isn't needed for stateless REST APIs
                .csrf(csrf -> csrf.disable())
                // Enable CORS using our custom configuration below
                .cors(withDefaults())
                // Set the session management to stateless (no HTTP sessions)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Allow all OPTIONS requests (preflight requests) for any endpoint
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        // Permit actuator endpoints so health checks, etc., can be accessed without auth
                        .requestMatchers("/actuator/**").permitAll()
                        // Require authentication for /api/** endpoints
                        .requestMatchers("/api/**").authenticated()
                        // Allow any other requests (e.g., public endpoints)
                        .anyRequest().permitAll()
                )
                // Enable HTTP Basic Authentication
                .httpBasic(withDefaults());

        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {
        UserDetails admin = User.withUsername("admin")
                .password(passwordEncoder.encode("admin123"))
                .roles("USER")
                .build();

        return new InMemoryUserDetailsManager(admin);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // Allow only your Netlify frontend origin
        configuration.setAllowedOrigins(List.of("https://scriptorium-ui.netlify.app"));
        // Allow HTTP methods needed by your frontend, including OPTIONS for preflight checks
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        // Allow the necessary headers (e.g., for Basic Auth and content type)
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        // Expose headers that the client might need to access
        configuration.setExposedHeaders(List.of("Authorization"));
        // Allow credentials to be included (cookies, auth headers, etc.)
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // Apply this CORS configuration for all endpoints
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
