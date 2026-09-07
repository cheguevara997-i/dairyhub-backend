package com.dairyhub.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CorsConfig {

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public CorsFilter corsFilter() {

        CorsConfiguration config = new CorsConfiguration();

        // Allow cookies/authorization information
        config.setAllowCredentials(true);

        // Frontend applications allowed to call the backend
        config.setAllowedOrigins(
                List.of(
                        "http://localhost:5173",
                        "https://dairyhub-five.vercel.app"
                )
        );

        // Allow request headers including Authorization
        config.setAllowedHeaders(
                List.of("*")
        );

        // Allow required HTTP methods
        config.setAllowedMethods(
                List.of(
                        "GET",
                        "POST",
                        "PUT",
                        "DELETE",
                        "OPTIONS"
                )
        );

        // Optional: allow these response headers to be read by frontend
        config.setExposedHeaders(
                List.of("Authorization")
        );

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration(
                "/**",
                config
        );

        return new CorsFilter(source);
    }
}