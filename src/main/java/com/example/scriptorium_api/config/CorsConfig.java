package com.example.scriptorium_api.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {

    private static final Logger logger = LoggerFactory.getLogger(CorsConfig.class);

    @Bean
    public CorsFilter corsFilter() {
        logger.info("✅ CorsFilter initialized!"); // Add logging to check if this runs

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
