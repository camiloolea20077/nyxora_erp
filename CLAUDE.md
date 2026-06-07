# Nyxora ERP — Guía del proyecto (CLAUDE.md)

ERP **monolito modular reactivo**. Stack: **Java 21 · Spring Boot 3.5 WebFlux · Spring Data R2DBC ·
PostgreSQL 16 · Flyway · MapStruct · springdoc/Swagger**. Base package: `com.cloud_tecnoligical.nyxora_erp`.

## Reglas obligatorias (leer antes de codificar)
- [.claude/rules/00-convenciones-backend.md](.claude/rules/00-convenciones-backend.md) — idioma por capa, reactivo, R2DBC minimalista, QueryRepository.
- [.claude/rules/01-multitenant-softdelete.md](.claude/rules/01-multitenant-softdelete.md) — `empresa_id` del JWT, cross-tenant=404, soft-delete.
- [.claude/rules/02-base-datos.md](.claude/rules/02-base-datos.md) — esquema `public`, migraciones Flyway, diccionario de datos.

## Agentes (`.claude/agents/`)
- **arquitecto** — decisiones de arquitectura y límites de módulos.
- **base-datos** — campos/tablas reales; consultar SIEMPRE antes de escribir SQL de una HU.
- **desarrollo-backend** — genera el vertical slice reactivo (v3 adaptado a R2DBC).
- **jwt-multitenant** — seguridad JWT + tenant en Reactor Context.
- **ciberseguridad** — revisión AppSec (fuga cross-tenant, inyección, secretos).

## Comandos (`.claude/commands/`)
- `/nueva-hu <descripción>` — documenta una Historia de Usuario.
- `/nueva-entidad <feature>` — genera el slice reactivo completo y compila.

## Documentación (`docs/`)
- `docs/analisis-erp-referencia/` — análisis del ERP de referencia y diseño del MVP (fases 00–12).
- `docs/arquitectura/` — ADRs y visión general.
- `docs/base-datos/` — diseño/diccionario de datos extendido.
- `docs/hu/` — Historias de Usuario (plantilla + proyectadas).
- `docs/api/` — documentación de la API (Swagger).

## Verificación
- Compilar: `./mvnw -q -B compile`. Tests: `./mvnw -B test`.
- Swagger UI (al ejecutar): `http://localhost:8080/swagger-ui.html`.

## Núcleo ya implementado
- Migraciones `V1`–`V3` (Administración + Común). Utils del equipo en `util/`. `TenantContext`
  reactivo + `TenantInfo`. `OpenApiConfig`. Pendiente: seguridad JWT, slices transaccionales (V4+).
