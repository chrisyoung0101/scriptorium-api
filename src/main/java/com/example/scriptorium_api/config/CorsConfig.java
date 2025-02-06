package com.example.scriptorium_api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedOrigin("https://scriptorium-ui.netlify.app"); // ✅ Allow frontend
        config.addAllowedMethod("*");  // ✅ Allow all methods (GET, POST, OPTIONS, etc.)
        config.addAllowedHeader("*");  // ✅ Allow all headers
        config.setAllowCredentials(true);  // ✅ Required for cookies

        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}
