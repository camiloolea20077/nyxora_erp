# HU-0018 — Talento Humano (empleado: satélites + evaluación de desempeño)

| Campo | Valor |
|---|---|
| **Código** | HU-0018 |
| **Módulo** | Talento Humano |
| **Estado** | En desarrollo (backend) |
| **Prioridad** | Media |
| **Dependencias** | HU-0002 (tercero / empleado), catálogo `nivel_estudio` y `municipio`; migración V20 |

## Historia
> **Como** responsable de talento humano
> **quiero** registrar la hoja de vida del empleado (estudios, familiares, historia laboral) y sus evaluaciones de desempeño
> **para** mantener la información del personal y su seguimiento.

## Criterios de aceptación (Gherkin)
```gherkin
Escenario: Satélites de la hoja de vida del empleado
  Dado un empleado (tercero con rol empleado) existente en la empresa
  Cuando agrego estudios, familiares e historia laboral vía /api/empleados/{empleadoId}/...
  Entonces quedan asociados al empleado y se listan por separado

Escenario: Programa y evaluación de desempeño
  Cuando creo un programa de evaluación y registro una evaluación de un empleado
  Entonces la evaluación queda con su calificación y referencia al programa y al empleado

Escenario: Aislamiento por empresa
  Cuando referencio un empleado o programa de otra empresa
  Entonces obtengo 404 (mismo mensaje "no encontrado")
```

## Reglas de negocio
- RN1: El **empleado es un `tercero`** (rol empleado); no hay tabla maestra de empleado. Los satélites
  (`empleado_estudio`, `empleado_familiar`, `empleado_historia_laboral`) cuelgan del `tercero` vía `empleado_id`.
- RN2: `nivel_estudio` es un **catálogo global** (id/código/nombre/activo), expuesto por el catálogo genérico
  (`/api/catalogos/nivel-estudio`).
- RN3: `evaluacion_programa` es un catálogo por empresa (código opcional único, soft-delete). `evaluacion_desempeno`
  referencia opcionalmente un programa y un empleado; ambos se validan contra la empresa cuando vienen informados.
- RN4: Multitenant `empresa_id` del JWT, cross-tenant=404, soft-delete. `empresa_id`/`empleado_id` nunca se
  aceptan en el body (el empleado va en el path; la empresa en el JWT).

## Datos
- **Entidades dueñas:** `empleado_estudio`, `empleado_familiar`, `empleado_historia_laboral`,
  `evaluacion_programa`, `evaluacion_desempeno` (V20). Catálogo `nivel_estudio` (V20, sembrado en V24).
  Reutiliza `tercero` (V5) y `municipio` (V4).

## Contrato de API
| Método | Ruta | Request |
|---|---|---|
| GET/POST/PUT/DELETE | `/api/empleados/{empleadoId}/estudios` | `Create/UpdateEmpleadoEstudioDto` |
| GET/POST/PUT/DELETE | `/api/empleados/{empleadoId}/familiares` | `Create/UpdateEmpleadoFamiliarDto` |
| GET/POST/PUT/DELETE | `/api/empleados/{empleadoId}/historias-laborales` | `Create/UpdateEmpleadoHistoriaLaboralDto` |
| CRUD + /list | `/api/evaluacion-programas` | `Create/UpdateEvaluacionProgramaDto` |
| CRUD + /list | `/api/evaluaciones-desempeno` | `Create/UpdateEvaluacionDesempenoDto` (filtros `empleadoId`/`programaId`) |
| list/CRUD | `/api/catalogos/nivel-estudio` | catálogo genérico |

## Preguntas abiertas
- Vínculo del empleado con vinculación/cargo/nómina: se aborda en Sprint 13 (Nómina I).
- Detalle por competencias/ítems de la evaluación (factores): fuera del alcance de v1 (solo calificación global).
- Adjuntos de la hoja de vida (diplomas, certificados): vía motor de adjuntos (HU-0006), no en este slice.
