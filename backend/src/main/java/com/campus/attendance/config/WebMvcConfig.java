package com.campus.attendance.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Paths;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    private final AppUploadProperties appUploadProperties;

    public WebMvcConfig(AppUploadProperties appUploadProperties) {
        this.appUploadProperties = appUploadProperties;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String basePath = Paths.get(appUploadProperties.getBaseDir()).toAbsolutePath().normalize().toString();
        String location = "file:" + basePath + "/";
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(location);
    }
}
