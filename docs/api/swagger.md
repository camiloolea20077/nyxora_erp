# API — Swagger / OpenAPI

La API del ERP se documenta con **springdoc-openapi** (variante WebFlux).

## Acceso (con la app en ejecución)
- **Swagger UI:** http://localhost:8080/swagger-ui.html
- **OpenAPI JSON:** http://localhost:8080/v3/api-docs

## Configuración
- Dependencia: `org.springdoc:springdoc-openapi-starter-webflux-ui`.
- Bean de metadatos y esquema de seguridad JWT: `config/OpenApiConfig.java`.
- Ajustes de UI/rutas: `src/main/resources/application.yml` (`springdoc.*`).

## Autenticación en Swagger
Definido el esquema `bearerAuth` (HTTP bearer, formato JWT). En la UI, botón **Authorize** →
pegar `Bearer <token>` obtenido de `POST /api/auth/login` (ver HU-0001).

## Convenciones de documentación de endpoints
- Anotar controllers/DTOs con `@Tag`, `@Operation`, `@Schema` (concisos, en español para descripciones).
- Toda respuesta es `ApiResponse<T>` (status, message, error, data).
- Rutas en inglés y plural: `/api/third-parties`, `/api/companies`, etc.
