# HU-0005 — Recursos de costeo

| Campo | Valor |
|---|---|
| **Código** | HU-0005 |
| **Módulo** | Costos (catálogo usado por Común) |
| **Estado** | En desarrollo |
| **Prioridad** | Media |
| **Dependencias** | HU-0001 (auth); migración V14 (recurso) |

## Historia
> **Como** usuario de Costos/Común
> **quiero** administrar el catálogo de recursos de costeo
> **para** poder asociarlos a productos, terceros y movimientos contables como driver de costo.

## Criterios de aceptación (Gherkin)
```gherkin
Escenario: Crear recurso
  Dado un recurso con código no registrado en mi empresa
  Cuando hago POST /api/recursos
  Entonces se crea con activo=true y empresa_id de mi token, y recibo 201

Escenario: Código duplicado
  Dado un recurso ya existente con ese código en mi empresa
  Cuando hago POST /api/recursos
  Entonces recibo 400 "Ya existe un recurso con ese código"

Escenario: Acceso cross-tenant
  Dado un recurso de otra empresa
  Cuando hago GET /api/recursos/{id}
  Entonces recibo 404 "Recurso no encontrado"
```

## Reglas de negocio
- RN1: `codigo` único por empresa (`UNIQUE(empresa_id, codigo)`).
- RN2: Soft-delete; `empresa_id`/`usuario_id` del JWT.

## Datos
- **Entidad dueña:** `recurso` (V14). Campos: id, empresa_id, codigo, nombre, tipo_recurso, driver, costo_adicional, descripcion, activo + auditoría.
- **Migración nueva requerida:** no (existe en V14).

## Contrato de API
| Método | Ruta | Request | Response |
|---|---|---|---|
| POST/PUT/DELETE/GET + /list | `/api/recursos` | `Create/UpdateRecursoRequestDto` | `ApiResponse<RecursoResponseDto>` |

## Multitenencia y seguridad
- `empresa_id` desde el JWT (TenantContext). Cross-tenant = 404.
