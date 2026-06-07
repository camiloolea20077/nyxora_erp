# HU-0002 — Gestión de terceros (CRUD + roles)

| Campo | Valor |
|---|---|
| **Código** | HU-0002 |
| **Módulo** | Común |
| **Estado** | Propuesta |
| **Prioridad** | Alta |
| **Dependencias** | HU-0001 (auth); migración V2 (tercero, tercero_rol) |

## Historia
> **Como** usuario de Común
> **quiero** crear, consultar, actualizar y desactivar terceros y asignarles roles
> **para** disponer de un maestro único de clientes/proveedores/empleados por empresa.

## Criterios de aceptación (Gherkin)
```gherkin
Escenario: Crear tercero
  Dado un tercero con identificación no registrada en mi empresa
  Cuando hago POST /api/third-parties
  Entonces se crea con activo=true y empresa_id de mi token, y recibo 201

Escenario: Identificación duplicada
  Dado un tercero ya existente con esa identificación en mi empresa
  Cuando hago POST /api/third-parties
  Entonces recibo 400 "Ya existe un tercero con esa identificación"

Escenario: Listado paginado con búsqueda
  Cuando hago POST /api/third-parties/list con page/rows/search
  Entonces recibo solo terceros de mi empresa, no eliminados, paginados

Escenario: Acceso cross-tenant
  Dado un tercero de otra empresa
  Cuando hago GET /api/third-parties/{id}
  Entonces recibo 404 "Tercero no encontrado"
```

## Reglas de negocio
- RN1: Identificación única por empresa (`UNIQUE(empresa_id, identificacion)`).
- RN2: Un tercero tiene 1..N roles (cliente/proveedor/empleado) sin duplicar identidad.
- RN3: Eliminar = soft-delete (`deleted_at`); nunca DELETE físico.
- RN4: `empresa_id`/`usuario_id` provienen del JWT, no del body.

## Datos (consultar agente base-datos)
- **Entidad dueña:** `tercero` (módulo Común) + `tercero_rol`.
- **Columnas `tercero`:** id, empresa_id, identificacion, tipo_identificacion, tipo_persona, nombre,
  activo + auditoría. (ver `.claude/data/diccionario-datos.md`)
- **Migración nueva requerida:** no (existe en V2).

## Contrato de API
| Método | Ruta | Request | Response |
|---|---|---|---|
| POST | `/api/third-parties` | `CreateThirdPartyRequestDto` | `ApiResponse<ThirdPartyResponseDto>` (201) |
| PUT | `/api/third-parties` | `UpdateThirdPartyRequestDto` | `ApiResponse<Boolean>` |
| DELETE | `/api/third-parties/{id}` | — | `ApiResponse<Boolean>` |
| GET | `/api/third-parties/{id}` | — | `ApiResponse<ThirdPartyResponseDto>` |
| POST | `/api/third-parties/list` | `PageableDto` | `ApiResponse<PageResponse<ThirdPartyTableDto>>` |

## Estados
`activo ↔ inactivo` (negocio) · `vigente → eliminado` (deleted_at).

## Preguntas abiertas
- ¿La gestión de roles (`tercero_rol`) es endpoint aparte o se envía embebida al crear el tercero?
- ¿Se permite reactivar un tercero soft-deleted?
