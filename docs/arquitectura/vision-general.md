# Arquitectura — Visión general (Nyxora ERP)

## Resumen
ERP **monolito modular reactivo**: un único despliegue Spring Boot, módulos como bounded contexts
con frontera de código, comunicación interna por servicios y **eventos de dominio in-process**.

## Stack
| Capa | Tecnología |
|---|---|
| Lenguaje | Java 21 |
| Web | Spring WebFlux (reactivo) |
| Persistencia | Spring Data R2DBC + `DatabaseClient` (SQL nativo) |
| Migraciones | Flyway (vía JDBC, solo al arranque) |
| BD | PostgreSQL 16, esquema `public` |
| Mapeo | MapStruct (EN↔ES) + ModelMapper |
| API docs | springdoc-openapi (Swagger WebFlux) |
| Seguridad | JWT multi-tenant (Spring Security WebFlux) |

## Decisiones clave (ADR resumidas)
- **AD-R1 — Reactivo puro (WebFlux + R2DBC), no JPA.** JPA es bloqueante; R2DBC mantiene el flujo
  no-bloqueante de extremo a extremo. Encaja con la filosofía "todo SQL nativo en QueryRepository".
- **AD-R2 — Monolito modular, no microservicios (v1).** La causa raíz del ERP de referencia fue la
  frontera blanda, no la falta de distribución. Frontera por paquetes + propiedad de entidades.
- **AD-R3 — Sin event store ni CQRS completo (v1).** Eventos de dominio in-process (reactivos).
- **AD-R4 — Tenant en Reactor Context, no ThreadLocal.** En WebFlux el hilo cambia entre operadores.
- **AD-R5 — Esquema `public` plano + `empresa_id`.** Convención del equipo; frontera de módulo en código.
- **AD-R6 — Saldos/kardex/edades = proyecciones recalculables; movimientos append-only.**

> El análisis completo y el diseño del MVP están en `../analisis-erp-referencia/` (fases 09–12).
> Las decisiones AD-1…AD-11 del doc 09 siguen vigentes; AD-2/AD-8 quedaron reconciliadas con la
> convención del equipo (esquema plano + `empresa_id`).

## Módulos del MVP
Administración · Común · Compras · Inventario · Facturación · Caja · Cartera · Contabilidad básica.
Propiedad de entidades y dependencias acíclicas: ver `../analisis-erp-referencia/09-validacion-diseno-mvp.md`.

## Flujo de un request (reactivo)
```
Controller (Mono<ResponseEntity<ApiResponse<T>>>)
  → Service (lee TenantContext.get(), aplica reglas, flatMap)
    → R2dbcRepository (save/findById)  +  QueryRepository (DatabaseClient, SQL nativo)
      → PostgreSQL
GlobalExceptionHandler formatea cualquier error en ApiResponse.
```
