package com.restaurant.menu.infrastructure.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        SecurityScheme bearerScheme = new SecurityScheme()
            .type(SecurityScheme.Type.HTTP)
            .scheme("bearer")
            .bearerFormat("JWT")
            .in(SecurityScheme.In.HEADER)
            .name("Authorization")
            .description("JWT token. Roles: ADMIN (full access), VIEWER (read-only), KITCHEN (kitchen operations). Obtain via POST /api/v1/auth/login or POST /api/v1/auth/register");

        return new OpenAPI()
            .info(new Info()
                .title("Menu API")
                .description("REST API for managing restaurant menus\n\nRoles: ADMIN (full access), VIEWER (read-only), KITCHEN (kitchen operations)")
                .version("1.0.0"))
            .components(new Components()
                .addSecuritySchemes("bearer-jwt", bearerScheme));
    }
}
