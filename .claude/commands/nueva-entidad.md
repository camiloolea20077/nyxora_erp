---
description: Genera el vertical slice reactivo completo de una entidad (entity, dtos, mapper, repos, service, controller)
---

Genera el vertical slice reactivo para la entidad/feature: **$ARGUMENTS**

Sigue el agente **desarrollo-backend** y las reglas en `.claude/rules/`. Antes de escribir SQL,
consulta los campos reales con el agente **base-datos** (`.claude/data/diccionario-datos.md`).

Genera, en `com.cloud_tecnoligical.nyxora_erp`:
1. `entity/<Entidad>Entity.java` — español snake_case, `@Table`/`@Id` de Spring Data Relational
   (NO JPA), auditoría completa + `empresa_id` (+`sede_id` si aplica).
2. `dto/<feature>/` — `Create*RequestDto`, `Update*RequestDto`, `*ResponseDto`, `*TableDto`
   (inglés camelCase + `jakarta.validation`; sin `empresa_id`/`sede_id`).
3. `mapper/<feature>/<X>Mapper.java` — MapStruct (`componentModel="spring"`), traducción EN↔ES,
   ignora id/empresa_id/auditoría.
4. `repository/<feature>/<X>R2dbcRepository.java` — `R2dbcRepository<Entity, Long>` vacío.
5. `repository/<feature>/<X>QueryRepository.java` — `DatabaseClient`, SQL nativo, **alias entre
   comillas camelCase** (`AS "personType"`), filtros `deleted_at IS NULL` + `empresa_id = :empresaId`,
   devuelve `Mono`/`Flux`.
6. `service/<X>Service.java` + `service/impl/<X>ServiceImpl.java` — lee tenant con
   `TenantContext.get()` y compone con `flatMap`; soft-delete; validación tenant → 404.
7. `controller/<X>Controller.java` — `/api/<plural>`, `Mono<ResponseEntity<ApiResponse<T>>>`, `@Valid`.

Al final: ejecuta `./mvnw -q -B compile` para verificar que compila.
