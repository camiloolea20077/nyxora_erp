# HU-0015 — Presupuesto (rubros, fuentes, CPC, ejecución CDP→pago, saldos/PAC)

| Campo | Valor |
|---|---|
| **Código** | HU-0015 |
| **Módulo** | Presupuesto |
| **Estado** | En desarrollo (backend) |
| **Prioridad** | Media |
| **Dependencias** | HU-0001 (vigencia), HU-0002 (tercero), HU-0004 (centro_costo, proyecto), HU-0005 (recurso); migración V15 |

## Historia
> **Como** responsable de presupuesto (sector público)
> **quiero** definir rubros con su apropiación y ejecutar la cadena CDP→compromiso→obligación→pago
> **para** controlar la disponibilidad presupuestal y el PAC.

## Criterios de aceptación (Gherkin)
```gherkin
Escenario: Definir rubro y apropiación
  Cuando creo un rubro presupuestal (jerárquico, imputable con maneja_movimiento)
  Y hago POST /api/saldos-presupuestales/apropiar con plan inicial y adiciones
  Entonces el saldo del rubro (año) refleja la apropiación neta

Escenario: Cadena de ejecución
  Dado un rubro imputable con apropiación
  Cuando registro afectaciones (disponibilidad→compromiso→obligacion→pago) en POST /api/afectaciones-presupuestales
  Y hago POST /api/saldos-presupuestales/recalcular?rubroId&anio
  Entonces el saldo agrega los valores ejecutados por tipo de operación

Escenario: PAC
  Cuando hago POST /api/pac-presupuestal con rubro, año, mes (1-12) y valor
  Entonces se define el Plan Anualizado de Caja de ese rubro/mes
```

## Reglas de negocio
- RN1: Rubro jerárquico; `nivel` derivado del padre; sólo los rubros con `maneja_movimiento=TRUE` aceptan afectaciones. Código único por (empresa, vigencia).
- RN2: Afectación **append-only** (`tipo_operacion` ∈ disponibilidad|compromiso|obligacion|pago|reconocimiento|recaudo). No se edita ni borra; las correcciones son nuevas afectaciones.
- RN3: Saldo presupuestal por (rubro, año, mes=0 anual): la **apropiación** (plan_inicial, adiciones, reducciones, aplazamientos, créditos, contracréditos) es entrada; la **ejecución** (disponibilidad/compromiso/obligación/pagado/reconocimientos/recaudos) es **proyección recalculable** desde las afectaciones. apropiacionNeta = plan + adiciones − reducciones + créditos − contracréditos − aplazamientos.
- RN4: PAC por (rubro, año, mes 1-12), upsert idempotente.
- RN5: Multitenant `empresa_id` del JWT, cross-tenant=404, soft-delete (catálogos).

## Datos
- **Entidades dueñas:** `fuente_financiamiento`, `cpc`, `rubro_presupuestal`, `afectacion_presupuestal`, `saldo_presupuestal`, `pac_presupuestal` (V15).

## Contrato de API
| Método | Ruta | Request |
|---|---|---|
| CRUD + /list | `/api/fuentes-financiamiento` | `Create/UpdateFuenteFinanciamientoRequestDto` |
| CRUD + /list | `/api/cpc` | `Create/UpdateCpcRequestDto` |
| CRUD + /list | `/api/rubros-presupuestales` | `Create/UpdateRubroPresupuestalRequestDto` |
| POST · GET/{id} · POST /rubro/{rubroId}/list | `/api/afectaciones-presupuestales` | `CreateAfectacionPresupuestalRequestDto` |
| POST /apropiar · POST /recalcular · GET · GET /rubro/{id} | `/api/saldos-presupuestales` | `ApropiarRubroRequestDto` |
| POST · GET | `/api/pac-presupuestal` | `PacUpsertRequestDto` |

## Preguntas abiertas
- ¿Validar disponibilidad dura (compromiso ≤ apropiación, obligación ≤ compromiso) al registrar la afectación? (v1: append-only sin bloqueo; el saldo refleja el estado y el control de cupo se difiere).
- Ejecución mensualizada del saldo (hoy se consolida anual en mes 0); el PAC sí es mensual.
- Nested set (izquierda/derecha) del rubro: se deja `nivel`; la numeración nested-set se difiere.
