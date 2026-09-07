package dairyhub_backend.config;

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

        CorsConfiguration config =
                new CorsConfiguration();

        // =========================================
        // ALLOW CREDENTIALS
        // =========================================

        config.setAllowCredentials(true);


        // =========================================
        // ALLOWED FRONTENDS
        // =========================================

        config.setAllowedOrigins(
                List.of(
                        "http://localhost:5173",
                        "https://dairyhub-five.vercel.app"
                )
        );


        // =========================================
        // ALLOWED HEADERS
        // =========================================

        config.setAllowedHeaders(
                List.of(
                        "Authorization",
                        "Content-Type",
                        "Accept",
                        "Origin",
                        "X-Requested-With"
                )
        );


        // =========================================
        // ALLOWED METHODS
        // =========================================

        config.setAllowedMethods(
                List.of(
                        "GET",
                        "POST",
                        "PUT",
                        "DELETE",
                        "PATCH",
                        "OPTIONS"
                )
        );


        // =========================================
        // EXPOSED HEADERS
        // =========================================

        config.setExposedHeaders(
                List.of(
                        "Authorization"
                )
        );


        // =========================================
        // REGISTER CORS FOR ALL ENDPOINTS
        // =========================================

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration(
                "/**",
                config
        );


        return new CorsFilter(source);
    }
}