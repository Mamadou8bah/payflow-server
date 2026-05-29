package com.mamadou.payflow.common.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.Components;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI 3.0 Configuration for Payflow API Documentation
 * Provides comprehensive API documentation accessible at:
 * - Swagger UI: http://localhost:5000/swagger-ui.html
 * - OpenAPI JSON: http://localhost:5000/v3/api-docs
 * - OpenAPI YAML: http://localhost:5000/v3/api-docs.yaml
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Payflow API")
                        .version("1.0.0")
                        .description("Comprehensive payment flow management system API. " +
                                "This API provides endpoints for managing transactions, transfers, wallets, " +
                                "user accounts, risk assessment, reconciliation, and webhook management.")
                        .contact(new Contact()
                                .name("Payflow Support")
                                .email("support@payflow.local")
                                .url("https://payflow.local"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html")))
                .components(new Components()
                        .addSecuritySchemes("bearer", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("JWT Bearer token authentication")))
                .addSecurityItem(new SecurityRequirement().addList("bearer"));
    }
}
