# Regla 00 — Convenciones de backend (Nyxora, WebFlux + R2DBC)

**Stack:** Java 21 · Spring Boot 3.5 WebFlux · Spring Data R2DBC · PostgreSQL 16 · Flyway (JDBC) ·
MapStruct · ModelMapper · Lombok (`@Getter`/`@Setter`, nunca `@Data`) · springdoc (Swagger).
Base package: `com.cloud_tecnoligical.nyxora_erp`.

## Idioma por capa
- BD y entidades: **español snake_case** (`tercero`, `empresa_id`).
- DTOs, servicios, controllers, variables: **inglés camelCase** (`fullName`, `ThirdPartyService`).
- Mensajes de error al usuario: **español**.

## Reactivo (no negociable)
- Todo el flujo del request es **reactivo**: `Mono`/`Flux`. Nada bloqueante (ni JPA, ni JDBC) salvo
  Flyway al arranque.
- Entidades: `@Table`/`@Id` de **Spring Data Relational** (NO JPA). El id lo genera la BD.
- Sin `@PrePersist`/`@PreUpdate`: setear `created_at`, `activo`, `usuario_creacion` en el Service.
- `TenantContext` es **reactivo** (Reactor Context), nunca ThreadLocal.

## JPA→R2DBC minimalista
- Repositorio = `R2dbcRepository<Entity, Long>` solo con `save`/`findById`. Sin métodos derivados largos.
- **Todo filtro** (soft-delete, multi-tenant, joins, búsqueda, paginación) va en **QueryRepository**
  con `DatabaseClient` y **SQL nativo** parametrizado, devolviendo `Mono`/`Flux`.
- Alias en SQL: **comillas + camelCase** (`AS "personType"`) por el `MapperRepository` (match exacto).

## Respuestas y errores
- Toda respuesta envuelta en `ApiResponse<T>` (status, message, error, data).
- Sin try/catch en controllers/servicios para errores esperables: lanzar `GlobalException(HttpStatus, msg)`;
  lo formatea `GlobalExceptionHandler`.
- Inyección por constructor (sin `@Autowired` en campo).

Ver también: [01-multitenant-softdelete](01-multitenant-softdelete.md), [02-base-datos](02-base-datos.md).
