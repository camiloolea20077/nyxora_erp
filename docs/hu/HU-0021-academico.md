# HU-0021 — Académico (programas, asignaturas, grupos, carga docente)

| Campo | Valor |
|---|---|
| **Código** | HU-0021 |
| **Módulo** | Académico |
| **Estado** | En desarrollo (backend) |
| **Prioridad** | Media |
| **Dependencias** | HU-0004/0005 (centro_costo), HU-0018 (nivel_estudio), HU-0019 (vinculación), HU-0019 (concepto_nomina); migración V22 |

## Historia
> **Como** coordinador académico
> **quiero** administrar programas, asignaturas, grupos y la carga docente
> **para** organizar la oferta académica y trasladar las horas dictadas a la nómina de los catedráticos.

## Criterios de aceptación (Gherkin)
```gherkin
Escenario: Catálogos académicos
  Cuando creo programas, asignaturas (asociadas a programas con semestre/créditos) y grupos
  Entonces quedan disponibles para la carga docente

Escenario: Carga docente → nómina
  Dada una carga académica de un docente (vinculación) con su detalle de horas
  Cuando hago POST /api/cargas-academicas/{id}/generar-novedad con concepto y valor por hora
  Entonces se publica el evento CargaDocenteRegistrada y la interfaz de nómina registra una novedad
  por (total horas × valor hora) en la vinculación del docente
```

## Reglas de negocio
- RN1: `programa_academico`, `asignatura` (código único), `grupo_academico` son catálogos por empresa (soft-delete).
- RN2: `asignatura_programa` asocia una asignatura a un programa con semestre/créditos (alta/baja idempotente).
- RN3: `carga_academica` (maestro + `carga_academica_detalle`, líneas asignatura/grupo/horas) es del docente
  (vinculación). El update reemplaza el detalle.
- RN4: **Interfaz a nómina por evento**: `generar-novedad` suma las horas del detalle, calcula `horas × valorHora`
  y publica `CargaDocenteRegistrada`; `InterfazNominaDocenteListener` crea la `novedad_nomina`. Origen `carga#{id}`.
- RN5: Multitenant `empresa_id` del JWT, cross-tenant=404, soft-delete.

## Datos
- **Entidades dueñas:** `programa_academico`, `asignatura`, `asignatura_programa`, `grupo_academico`,
  `carga_academica`, `carga_academica_detalle` (V22). Reutiliza `centro_costo` (V7), `nivel_estudio` (V20),
  `vinculacion`/`concepto_nomina` (V21).

## Contrato de API
| Método | Ruta | Request |
|---|---|---|
| CRUD + /list | `/api/programas-academicos` | `Create/UpdateProgramaAcademicoRequestDto` |
| CRUD + /list + programas | `/api/asignaturas` (+ `/{id}/programas`) | `Create/UpdateAsignaturaRequestDto`, `CreateAsignaturaProgramaDto` |
| CRUD + /list | `/api/grupos-academicos` | `Create/UpdateGrupoAcademicoRequestDto` |
| CRUD + /list | `/api/cargas-academicas` | `Create/UpdateCargaAcademicaRequestDto` (con detalle) |
| POST /{id}/generar-novedad | `/api/cargas-academicas` | `GenerarNovedadDocenteRequestDto` (concepto + valor hora) |

## Preguntas abiertas
- Cálculo de la liquidación de catedráticos a partir de la novedad: lo resuelve Nómina II (Sprint 14) al liquidar.
- Validación de que la vinculación sea de tipo "catedrático/docente": v1 acepta cualquier vinculación activa.
- Horarios/aulas y matrícula de estudiantes: fuera del alcance de v1.
