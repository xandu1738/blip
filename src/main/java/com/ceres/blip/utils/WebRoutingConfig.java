package com.ceres.blip.utils;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.server.ErrorPage;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.PathResourceResolver;
import org.springframework.web.servlet.resource.ResourceResolverChain;

import java.util.List;

@Configuration
public class WebRoutingConfig implements WebMvcConfigurer {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // When a route is not found, forward to index.html (for SPA)
        registry.addViewController("/urlNotFound")
                .setViewName("forward:/index.html");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Serve frontend static files (ONLY for frontend paths)
        registry.addResourceHandler(
                        "/",
                        "/index.html",
                        "/favicon.ico",
                        "/static/**",
                        "/assets/**") // adjust if your frontend has other folders
                .addResourceLocations("classpath:/static/")
                .resourceChain(false)
                .addResolver(new PathResourceResolver() {
                    @Override
                    public Resource resolveResource(HttpServletRequest request,
                                                    @NonNull String requestPath,
                                                    @NonNull List<? extends Resource> locations,
                                                    @NonNull ResourceResolverChain chain) {
                        // Try to find the resource
                        Resource resource = super.resolveResource(request, requestPath, locations, chain);
                        if (resource != null) {
                            return resource;
                        }
                        // Fallback to index.html (SPA)
                        return super.resolveResource(request, "index.html", locations, chain);
                    }
                });
    }

    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        configurer.setUseSuffixPatternMatch(false);
    }

    @Bean
    public WebServerFactoryCustomizer<ConfigurableServletWebServerFactory> containerCustomizer() {
        return container -> container.addErrorPages(
                new ErrorPage(HttpStatus.NOT_FOUND, "/urlNotFound")
        );
    }
}

