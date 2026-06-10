# HU-0004 — Estructura organizacional (centro de costo, dependencia, proyecto)

| Campo | Valor |
|---|---|
| **Código** | HU-0004 |
| **Módulo** | Común |
| **Estado** | En desarrollo |
| **Prioridad** | Alta |
| **Dependencias** | HU-0001 (auth); migración V7 (centro_costo, dependencia, proyecto); V1 (sede), V5 (tercero) |

## Historia
> **Como** usuario de Común
> **quiero** administrar centros de costo, dependencias y proyectos jerárquicos
> **para** clasificar la operación contable, presupuestal y de inventario por unidad organizativa.

## Criterios de aceptación (Gherkin)
```gherkin
Escenario: Crear centro de costo jerárquico
  Dado un centro de costo padre de mi empresa (o ninguno para raíz)
  Cuando hago POST /api/centros-costo
  Entonces se crea con activo=true y empresa_id de mi token, y recibo 201

Escenario: Crear dependencia bajo un centro de costo
  Dado un centro de costo de mi empresa
  Cuando hago POST /api/dependencias con centroCostoId
  Entonces la dependencia queda asociada al centro de costo

Escenario: Crear proyecto
  Cuando hago POST /api/proyectos con fechas y código único
  Entonces se crea el proyecto de mi empresa

Escenario: Código duplicado
  Dado un código ya existente en mi empresa para la entidad
  Cuando hago POST
  Entonces recibo 400 "Ya existe … con ese código"

Escenario: Acceso cross-tenant
  Dado un registro de otra empresa
  Cuando hago GET /{id}
  Entonces recibo 404 "… no encontrado"
```

## Reglas de negocio
- RN1: `codigo` único por empresa en `centro_costo`, `dependencia`, `proyecto`.
- RN2: Jerarquía por autoreferencia (`centro_costo_padre_id`, `dependencia_padre_id`) dentro de la misma empresa.
- RN3: `dependencia.centro_costo_id` y `centro_costo.tercero_id`/`sede_id` deben pertenecer a la empresa.
- RN4: `centro_costo.es_observacion=true` ⇒ nodo hoja imputable (recibe movimientos).
- RN5: `proyecto.fecha_final ≥ fecha_inicio` cuando ambas están presentes.
- RN6: Soft-delete; `empresa_id`/`usuario_id` del JWT.

## Datos
- **Entidades dueñas:** `centro_costo`, `dependencia`, `proyecto` (V7).
- **Migración nueva requerida:** no (existen en V7).

## Contrato de API
| Método | Ruta | Request | Response |
|---|---|---|---|
| POST/PUT/DELETE/GET + /list | `/api/centros-costo` | `Create/UpdateCentroCostoRequestDto` | `ApiResponse<CentroCostoResponseDto>` |
| POST/PUT/DELETE/GET + /list | `/api/dependencias` | `Create/UpdateDependenciaRequestDto` | `ApiResponse<DependenciaResponseDto>` |
| POST/PUT/DELETE/GET + /list | `/api/proyectos` | `Create/UpdateProyectoRequestDto` | `ApiResponse<ProyectoResponseDto>` |

## Multitenencia y seguridad
- `empresa_id` desde el JWT (TenantContext). Cross-tenant = 404.

## Preguntas abiertas
- El campo `nivel`/`izquierda`/`derecha` (nested set) se mantiene editable manual por ahora; el recálculo automático del árbol se difiere.
