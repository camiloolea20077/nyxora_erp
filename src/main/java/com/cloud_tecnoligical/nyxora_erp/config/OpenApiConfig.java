package com.cloud_tecnoligical.nyxora_erp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

/**
 * Configuración de OpenAPI/Swagger para documentar la API del ERP.
 * UI: /swagger-ui.html · JSON: /v3/api-docs
 */
@Configuration
public class OpenApiConfig {

    private static final String BEARER = "bearerAuth";

    @Bean
    public OpenAPI nyxoraOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("Nyxora ERP API")
                .description("API del ERP Nyxora (monolito modular reactivo). Multi-tenant por empresa_id.")
                .version("v0.1.0"))
            .addSecurityItem(new SecurityRequirement().addList(BEARER))
            .components(new Components().addSecuritySchemes(BEARER,
                new SecurityScheme()
                    .type(SecurityScheme.Type.HTTP)
                    .scheme("bearer")
                    .bearerFormat("JWT")
                    .description("JWT multi-tenant. Enviar: Authorization: Bearer <token>")));
    }
}
