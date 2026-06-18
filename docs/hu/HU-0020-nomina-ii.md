# HU-0020 — Nómina II (liquidación + detalle + PILA + interfaz contable)

| Campo | Valor |
|---|---|
| **Código** | HU-0020 |
| **Módulo** | Nómina |
| **Estado** | En desarrollo (backend) |
| **Prioridad** | Alta |
| **Dependencias** | HU-0019 (cargo/grupo/vinculación/concepto/novedad), HU-0007 (cuenta/periodo/comprobante); migración V21 |

## Historia
> **Como** responsable de nómina
> **quiero** liquidar la nómina de un periodo (generando el detalle por empleado/concepto y los aportes PILA) y contabilizarla
> **para** obtener la nómina del periodo y reflejarla en contabilidad.

## Criterios de aceptación (Gherkin)
```gherkin
Escenario: Liquidar una nómina abierta
  Dada una liquidación en estado 'abierto' y vinculaciones activas con novedades pendientes
  Cuando hago POST /api/liquidaciones-nomina/{id}/liquidar
  Entonces se genera el detalle (una línea por novedad + sueldo) y los aportes PILA (salud, pensión)
  Y la liquidación pasa a estado 'liquidado' y las novedades quedan marcadas como aplicadas

Escenario: Contabilizar (interfaz contable por evento)
  Dada una liquidación en estado 'liquidado'
  Cuando hago POST /api/liquidaciones-nomina/{id}/contabilizar con periodo y cuentas
  Entonces se publica un AsientoContableSolicitado (gasto débito; deducciones y neto por pagar crédito)
  Y la interfaz contable genera el comprobante; la liquidación pasa a 'contabilizado'

Escenario: Append-only
  El detalle y los aportes PILA no se editan ni se borran; las correcciones se hacen con reversa (anular + re-liquidar).
```

## Reglas de negocio
- RN1: `liquidacion_nomina` (encabezado) tiene máquina de estados `abierto → liquidado → contabilizado` (+ `revisado`,
  `cerrado`, `anulado`, CHECK en BD). Solo se edita/elimina/liquida en `abierto`.
- RN2: **liquidar** (transaccional) genera, append-only:
  - `liquidacion_nomina_detalle`: una línea por **novedad pendiente** (`fecha_aplicada IS NULL`, no anulada) de las
    vinculaciones activas del grupo; opcionalmente una línea de **sueldo básico** por vinculación si se envía `conceptoSueldoId`.
  - `aporte_pila`: salud (4% / 8.5%) y pensión (4% / 12%) sobre el IBC (= sueldo) por empleado.
  - Marca las novedades incluidas con `fecha_aplicada` para no recontarlas.
- RN3: **contabilizar** publica `AsientoContableSolicitado` (bus de dominio): débito a la cuenta de gasto por los
  devengados; crédito a deducciones (si hay y se indica cuenta) y a la cuenta por pagar por el neto. La
  `InterfazContableListener` crea y confirma el comprobante. Origen `nomina#{id}`.
- RN4: Multitenant `empresa_id` del JWT, cross-tenant=404, soft-delete (solo encabezado). El detalle/PILA son append-only.

## Datos
- **Entidades dueñas:** `liquidacion_nomina`, `liquidacion_nomina_detalle` (append-only), `aporte_pila` (append-only) (V21).
  Reutiliza `vinculacion`, `novedad_nomina`, `concepto_nomina`, `grupo_nomina` (V21), `cuenta`/`periodo_contable`/`comprobante` (V8).

## Contrato de API
| Método | Ruta | Request |
|---|---|---|
| CRUD + /list | `/api/liquidaciones-nomina` | `Create/UpdateLiquidacionNominaRequestDto` (filtro `grupoNominaId`) |
| POST /{id}/liquidar | `/api/liquidaciones-nomina` | `LiquidarNominaRequestDto` (`conceptoSueldoId?`) |
| POST /{id}/contabilizar | `/api/liquidaciones-nomina` | `ContabilizarNominaRequestDto` (periodo + cuentas) |
| POST /{id}/anular | `/api/liquidaciones-nomina` | — |
| GET /{id}/detalle · GET /{id}/pila | `/api/liquidaciones-nomina` | — |

## Preguntas abiertas
- **Motor de fórmulas** del concepto: v1 toma los valores de las novedades (`cantidad_valor`) y del sueldo de la
  vinculación; la evaluación de `concepto.formula` como expresión se difiere.
- **Interfaz presupuestal por evento** (afectación CDP→compromiso→obligación): diferida; v1 cubre la interfaz contable.
- Reversa formal de una liquidación contabilizada (asiento de reverso): diferida; v1 usa `anular`.
- PILA: solo salud y pensión con porcentajes fijos; ARL/CCF/SENA/ICBF y novedades de IBC se difieren.
