package com.emailConnecter.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuration class for setting up web application configurations like CORS mapping.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * Adds Cross-Origin Resource Sharing (CORS) mappings to allow requests from any origin.
     *
     * @param registry the CorsRegistry used to define mapping endpoints
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE")
                .allowedHeaders("*");
    }
}
