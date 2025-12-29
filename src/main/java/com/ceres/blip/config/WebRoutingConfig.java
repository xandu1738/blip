package com.ceres.blip.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.server.ErrorPage;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class WebRoutingConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(@NonNull ResourceHandlerRegistry registry) {
        // Configure path for uploaded files outside project build
        Path uploadPath = Paths.get(System.getProperty("user.dir"), "web", "upload");
        String uploadLocation = uploadPath.toUri().toString();

        // Ensure the directory exists
        File uploadDir = uploadPath.toFile();
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        // Map /upload/** URLs to the external upload directory
        registry.addResourceHandler("/upload/**")
                .addResourceLocations(uploadLocation)
                .setCachePeriod(3600) // Cache for 1 hour
                .resourceChain(false); // Set to false for external files

        // Configure static resources from classpath (CSS, JS, images, etc.)
        registry.addResourceHandler("/static/**")
                .addResourceLocations("classpath:/static/")
                .setCachePeriod(3600)
                .resourceChain(true);

        registry.addResourceHandler("/public/**")
                .addResourceLocations("classpath:/public/")
                .setCachePeriod(3600)
                .resourceChain(true);

        // Fallback for any other static content
        registry.addResourceHandler("/assets/**")
                .addResourceLocations("classpath:/assets/")
                .setCachePeriod(3600)
                .resourceChain(true);
    }
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/urlNotFound")
                .setViewName("forward:index.html");
    }


    @Bean
    public WebServerFactoryCustomizer<ConfigurableServletWebServerFactory> containerCustomizer() {
        return container -> container.addErrorPages(new ErrorPage(HttpStatus.NOT_FOUND, "/urlNotFound"));
    }
}
