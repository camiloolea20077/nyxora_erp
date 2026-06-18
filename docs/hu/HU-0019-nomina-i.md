# HU-0019 — Nómina I (cargo, grupo, vinculación, concepto, novedades)

| Campo | Valor |
|---|---|
| **Código** | HU-0019 |
| **Módulo** | Nómina |
| **Estado** | En desarrollo (backend) |
| **Prioridad** | Alta |
| **Dependencias** | HU-0002 (tercero/empleado), HU-0007 (cuenta), HU-0015 (rubro/fuente); migración V21 |

## Historia
> **Como** responsable de nómina
> **quiero** definir cargos, grupos de nómina, conceptos (con fórmula) y registrar las vinculaciones y novedades de los empleados
> **para** preparar el cálculo de la liquidación (Nómina II).

## Criterios de aceptación (Gherkin)
```gherkin
Escenario: Catálogos base de nómina
  Cuando creo cargos, grupos de nómina y conceptos
  Entonces quedan disponibles para asociarlos a las vinculaciones y novedades

Escenario: Vincular un empleado
  Dado un empleado (tercero) existente en la empresa
  Cuando creo una vinculación con su cargo, grupo, sueldo y fechas
  Entonces queda registrada en estado 'activa'

Escenario: Registrar novedad / embargo
  Dada una vinculación y un concepto
  Cuando registro una novedad (p. ej. un embargo con expediente y demandante)
  Entonces queda 'registrada' y puede anularse con POST /api/novedades-nomina/{id}/anular

Escenario: Aislamiento por empresa
  Cuando referencio un empleado, cargo, grupo, vinculación o concepto de otra empresa
  Entonces obtengo 404/400 (mismo mensaje "no encontrado")
```

## Reglas de negocio
- RN1: `cargo` y `grupo_nomina` son catálogos por empresa (código único, soft-delete).
- RN2: `concepto_nomina` tiene **clase** ∈ {`devengado`,`deduccion`,`provision`,`aporte`} (validada) y una `formula`
  (texto, se evalúa en Nómina II). Código único por empresa. FKs contables/presupuestales opcionales.
- RN3: `vinculacion` requiere un **empleado** (tercero de la empresa); valida `cargo`, `grupo_nomina` y `jefe` si vienen.
  Arranca en `estado_vinculacion = 'activa'`.
- RN4: `novedad_nomina` requiere `vinculacion` + `concepto` de la empresa; soporta embargos (tipo, expediente,
  demandante, banco, cuenta). Acción **anular** (no se edita una novedad anulada).
- RN5: Multitenant `empresa_id` del JWT, cross-tenant=404, soft-delete. `empresa_id`/`usuario_*` nunca en el body
  (se toman del JWT). `usuario_creacion`/`usuario_modificacion` se setean en el service.

## Datos
- **Entidades dueñas:** `cargo`, `grupo_nomina`, `concepto_nomina`, `vinculacion`, `novedad_nomina` (V21).
  Reutiliza `tercero` (V5), `cuenta` (V8), `rubro_presupuestal`/`fuente_financiamiento` (V15), `banco`, `sede`,
  `dependencia`, `municipio`. La liquidación (`liquidacion_nomina` + detalle, `aporte_pila`) es **Sprint 14**.

## Contrato de API
| Método | Ruta | Request |
|---|---|---|
| CRUD + /list | `/api/cargos` | `Create/UpdateCargoRequestDto` |
| CRUD + /list | `/api/grupos-nomina` | `Create/UpdateGrupoNominaRequestDto` |
| CRUD + /list | `/api/conceptos-nomina` | `Create/UpdateConceptoNominaRequestDto` (filtro `clase`) |
| CRUD + /list | `/api/vinculaciones` | `Create/UpdateVinculacionRequestDto` (filtro `empleadoId`) |
| CRUD + /list + /{id}/anular | `/api/novedades-nomina` | `Create/UpdateNovedadNominaRequestDto` (filtro `vinculacionId`) |

## Preguntas abiertas
- Evaluación de la `formula` del concepto (motor de cálculo): se aborda en Nómina II (Sprint 14).
- Validación estricta de las FKs contables/presupuestales del concepto: diferida (las enforce la BD).
- Tipos de vinculación/cotizante/ausentismo como catálogos finos: v1 usa texto libre.
