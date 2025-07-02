package com.chatapp.chatappbackend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // Allow CORS for all endpoints
                .allowedOrigins("*") // Allow all origins
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")
                .allowedHeaders("*")
                .maxAge(3600); // 1 hour
    }
    
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Map the URL path /uploads/** to the physical location of files in the classpath
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("classpath:/static/uploads/");
        
        // Also serve files from the external directory if that's where uploads are going
        registry.addResourceHandler("/external-uploads/**")
                .addResourceLocations("file:./uploads/");
    }
}