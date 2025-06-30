package com.example.bankingsystem.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuration class to enable Cross-Origin Resource Sharing (CORS) for the Spring Boot application.
 * This allows web applications served from different origins (e.g., a frontend running on a different port)
 * to make requests to this backend API.
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    /**
     * Configures CORS mappings.
     * @param registry The CorsRegistry to add CORS configurations to.
     */
    @Override
    public void addCorsMappings(CorsRegistry registry){
        registry.addMapping("/api/**") // Apply CORS to all paths under /api/
                .allowedOrigins("http://localhost:3000","http://13.60.84.199","http://ec2-13-60-84-199.eu-north-1.compute.amazonaws.com") // Allow requests from your frontend's origin
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // Allow specified HTTP methods
                .allowedHeaders("*") // Allow all headers
                .allowCredentials(true) // Allow credentials (cookies, HTTP authentication)
                .maxAge(3600); // How long the preflight request can be cached (in seconds)
    }
}
