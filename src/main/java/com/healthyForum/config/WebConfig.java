package com.healthyForum.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Serve local files from /uploads/** URL
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:/C:/Users/admin/Downloads/healthyForum/healthyForum/healthyForum/Uploads/");
    }
}
