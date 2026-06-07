# Fase 12 — Vertical slice de referencia: módulo `tercero`

> **Propósito.** Implementar el primer corte vertical completo del backend siguiendo el **estándar
> del equipo (Spring Boot v3)**, para que sirva de **plantilla** del resto de entidades. Se eligió
> `tercero` por ser el maestro central del módulo Común.
>
> **Estado:** compila (`mvn -B compile` ✅, Java 17 target / JDK 21, MapStruct + Lombok OK).

---

## 1. Qué se generó

### Infraestructura compartida (se crea una sola vez)

```
backend/
├── pom.xml                                  # Spring Boot 3.3.5, Java 17, JPA, Flyway, MapStruct, Lombok, Validation
└── src/main/java/com/erp/mvp/
    ├── ErpMvpApplication.java
    ├── dto/common/
    │   ├── ApiResponse.java                 # wrapper único de respuesta
    │   └── PageableRequestDto.java          # page/rows/search/orderBy/order
    ├── util/
    │   ├── GlobalException.java             # excepción de negocio + HttpStatus
    │   ├── GlobalExceptionHandler.java      # @RestControllerAdvice -> ApiResponse
    │   └── MapperRepository.java            # filas SQL (snake) -> DTO (camel) por reflexión
    └── security/
        ├── TenantInfo.java                  # empresaId/usuarioId/sedeId/superAdmin
        └── TenantContext.java               # ThreadLocal; lo puebla el filtro JWT
```

### Slice de `tercero` (patrón a replicar por entidad)

```
src/main/java/com/erp/mvp/
├── entity/TerceroEntity.java                       # español snake_case
├── dto/thirdparty/
│   ├── CreateThirdPartyRequestDto.java             # inglés camelCase + validación
│   ├── UpdateThirdPartyRequestDto.java
│   ├── ThirdPartyResponseDto.java
│   └── ThirdPartyTableDto.java
├── mapper/thirdparty/ThirdPartyMapper.java         # MapStruct, traducción EN<->ES
├── repository/thirdparty/
│   ├── ThirdPartyJpaRepository.java                # VACÍO (solo extends JpaRepository)
│   └── ThirdPartyQueryRepository.java              # TODO el filtrado en SQL nativo
├── service/
│   ├── ThirdPartyService.java
│   └── impl/ThirdPartyServiceImpl.java
└── controller/ThirdPartyController.java            # /api/third-parties, ApiResponse<T>
```

---

## 2. Cómo se respetó cada regla v3

| Regla v3 | Dónde se ve |
|---|---|
| **Idioma por capa** | Entidad `TerceroEntity` con campos `tipo_identificacion`, `empresa_id` (español snake). DTOs `identificationType`, `personType` (inglés camel). Servicios/controllers en inglés. |
| **Auditoría estandarizada** | Entidad con `created_at`/`updated_at`/`deleted_at` + `usuario_creacion`/`usuario_modificacion` + `activo`; `@PrePersist`/`@PreUpdate`. |
| **JPA minimalista** | `ThirdPartyJpaRepository` no tiene **ningún** método derivado. |
| **QueryRepository = único filtrado** | `existsActive...`, `findActiveById`, `listThirdParties` con `deleted_at IS NULL` + `empresa_id = :empresa_id` en SQL nativo. |
| **Soft-delete** | `delete()` setea `deleted_at = now()` + `usuario_modificacion`; nunca `DELETE` físico. |
| **Multi-tenant desde TenantContext** | `empresa_id`/`usuario_id` se leen de `TenantContext`; los DTOs de request **no** los aceptan. |
| **Cross-tenant = 404** | `validateTenantAndNotDeleted` lanza 404 con el mismo mensaje "no encontrado". |
| **Lombok `@Getter`/`@Setter`** | Sin `@Data` en ninguna clase. |
| **Inyección por constructor** | Todos los componentes; sin `@Autowired` en campo. |
| **Respuesta en `ApiResponse<T>`** | Controller siempre envuelve en `ApiResponse`; sin try/catch (lo maneja el advice). |
| **Mensajes en español** | Validaciones y `GlobalException` en español. |

---

## 3. Contrato QueryRepository ↔ MapperRepository (importante para replicar)

`MapperRepository` mapea **por nombre** filas crudas a DTOs convirtiendo `snake_case → camelCase`.
Por eso el SELECT del QueryRepository **aliasa cada columna** al nombre semántico del campo del DTO:

```sql
SELECT t.tipo_identificacion AS identification_type,   -- -> identificationType
       t.tipo_persona        AS person_type,            -- -> personType
       t.created_at           AS created_at              -- -> createdAt
FROM tercero t
WHERE t.empresa_id = :empresa_id AND t.deleted_at IS NULL
```

- Columnas sin campo destino (p. ej. `total_rows` del `COUNT(*) OVER()`) se **ignoran** sin error.
- El `ORDER BY` usa **lista blanca** (`SORTABLE`) para evitar inyección por columna dinámica.

> 🧠 Regla práctica al crear un nuevo slice: el alias SQL debe ser el **snake_case** equivalente al
> campo camelCase del DTO. Así el mapeo es automático y no hay que tocar `MapperRepository`.

---

## 4. Endpoints expuestos

| Método | Ruta | Body | Respuesta |
|---|---|---|---|
| POST | `/api/third-parties` | `CreateThirdPartyRequestDto` | `ApiResponse<ThirdPartyResponseDto>` (201) |
| PUT | `/api/third-parties` | `UpdateThirdPartyRequestDto` | `ApiResponse<Boolean>` |
| DELETE | `/api/third-parties/{id}` | — | `ApiResponse<Boolean>` |
| GET | `/api/third-parties/{id}` | — | `ApiResponse<ThirdPartyResponseDto>` |
| POST | `/api/third-parties/list` | `PageableRequestDto` | `ApiResponse<PageImpl<ThirdPartyTableDto>>` |

---

## 5. Pendiente / dependencias conocidas

1. **Filtro JWT (`JwtAuthenticationFilter`)** que pueble `TenantContext` por request: lo aporta el
   estándar de seguridad del equipo (`agente_jwt_multitenant.md`). Sin él, cualquier endpoint lanza
   `IllegalStateException("TenantContext no inicializado")`. **No se creó aquí** para no chocar con
   ese diseño.
2. **`tercero_rol` (cliente/proveedor/empleado):** este slice cubre la tabla `tercero`. La gestión de
   roles (colección hija) se deja para un slice siguiente, porque el estándar v3 muestra el patrón de
   **entidad simple** y conviene confirmar cómo prefieres manejar colecciones hijas antes de fijarlo.
3. **`empresa` (tabla raíz):** su `validateTenant` es especial (super-admin ve todas; un usuario
   normal solo la suya), distinto al patrón `empresa_id` de las demás tablas.

---

## 6. Cómo replicar el patrón para otra entidad

1. Crear `XEntity` (español snake_case + auditoría estándar).
2. Crear DTOs en inglés camelCase (`CreateXRequestDto`, `UpdateXRequestDto`, `XResponseDto`, `XTableDto`).
3. `XMapper` (MapStruct) con `@Mapping` explícito EN↔ES, ignorando id/empresa_id/auditoría.
4. `XJpaRepository` vacío.
5. `XQueryRepository` con `existsActive...`, `findActiveById`, `listX` (SQL nativo, alias snake→camel, tenant + soft-delete).
6. `XService` + `XServiceImpl` (create/update/delete/findById/list + `validateTenantAndNotDeleted`).
7. `XController` (`/api/x`, `ApiResponse<T>`, inyección por constructor).

---

### Rastro del trabajo
Código generado y **compilado** el 2026-06-06 con Maven 3.9.9 / JDK 21 (target 17). No se ejecutó la
aplicación (requiere PostgreSQL con las migraciones de la Fase 11 y el filtro JWT que puebla
`TenantContext`).
