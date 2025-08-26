package com.ceres.blip.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfiguration {
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Blip API")
                        .version("v1.0")
                        .description("API endpoints for Blip Transport and Logistics System")
                );
    }
}
