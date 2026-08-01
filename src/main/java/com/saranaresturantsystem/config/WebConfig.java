package com.saranaresturantsystem.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/api/v1/**") // Applies to all routes under api/v1
                        .allowedOrigins("http://localhost:5173") // Your React Dev Server
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS" , "PATCH")
                        .allowedHeaders("*")
                        .allowCredentials(true);
            }
        };
    }
}