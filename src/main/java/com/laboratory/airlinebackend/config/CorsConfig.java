package com.laboratory.airlinebackend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration
@EnableWebMvc
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // Allow CORS for all paths
                .allowedOrigins("http://localhost:5173") // Specify the origin you want to allow
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // Specify the HTTP methods you want to allow
                .allowedHeaders("*") // Allow all headers
                .exposedHeaders("Authorization") // Expose any custom headers you need
                .allowCredentials(true); // Allow cookies and credentials
    }
}