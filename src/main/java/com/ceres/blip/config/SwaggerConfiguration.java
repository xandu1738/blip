package com.ceres.blip.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfiguration {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Blip API")
                        .version("v1.0")
                        .description("API endpoints for Blip Transport and Logistics System")
                ).servers(List.of(
                        new Server()
                                .url("http://localhost:7071")
                                .description("Development")
                ));
    }
}
