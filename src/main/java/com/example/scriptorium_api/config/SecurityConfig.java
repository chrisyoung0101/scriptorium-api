//package com.example.scriptorium_api.config;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.http.HttpMethod;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.http.SessionCreationPolicy;
//import org.springframework.security.core.userdetails.User;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.security.provisioning.InMemoryUserDetailsManager;
//import org.springframework.security.web.SecurityFilterChain;
//import org.springframework.web.cors.CorsConfiguration;
//import org.springframework.web.cors.CorsConfigurationSource;
//import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
//
//import java.util.List;
//
//import static org.springframework.security.config.Customizer.withDefaults;
//
//@Configuration
//public class SecurityConfig {
//
//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http
//                // Disable CSRF protection as it's not needed for stateless REST APIs
//                .csrf(csrf -> csrf.disable())
//                // Enable CORS using the custom configuration defined below
//                .cors(withDefaults())
//                // Set session management to stateless
//                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//                .authorizeHttpRequests(auth -> auth
//                        // Allow all OPTIONS (preflight) requests without authentication
//                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
//                        // Allow actuator endpoints without authentication
//                        .requestMatchers("/actuator/**").permitAll()
//                        // Require authentication for /api/** endpoints
//                        .requestMatchers("/api/**").authenticated()
//                        // Allow all other requests (e.g., public resources)
//                        .anyRequest().permitAll()
//                )
//                // Enable HTTP Basic authentication
//                .httpBasic(withDefaults());
//
//        return http.build();
//    }
//
//    @Bean
//    public UserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {
//        // Create an in-memory user with username "admin" and password "admin123"
//        UserDetails admin = User.withUsername("admin")
//                .password(passwordEncoder.encode("admin123"))
//                .roles("USER")
//                .build();
//
//        return new InMemoryUserDetailsManager(admin);
//    }
//
//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        // Use BCrypt for password encoding
//        return new BCryptPasswordEncoder();
//    }
//
//    @Bean
//    public CorsConfigurationSource corsConfigurationSource() {
//        CorsConfiguration configuration = new CorsConfiguration();
//        // Using allowedOriginPatterns to be flexible with the exact format of the Origin header
//        configuration.setAllowedOriginPatterns(List.of("https://scriptorium-ui.netlify.app"));
//        // Alternatively, if you know the exact format, you can use:
//        // configuration.setAllowedOrigins(List.of("https://scriptorium-ui.netlify.app"));
//
//        // Allow all headers (this will let any header be sent in the preflight request)
//        configuration.setAllowedHeaders(List.of("*"));
//        // Allow the necessary HTTP methods including OPTIONS for preflight
//        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
//        // Expose headers that might be useful to the client
//        configuration.setExposedHeaders(List.of("Authorization"));
//        // Allow credentials (cookies, auth headers, etc.)
//        configuration.setAllowCredentials(true);
//
//        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        // Apply the configuration for all endpoints
//        source.registerCorsConfiguration("/**", configuration);
//        return source;
//    }
//}
