# HU-0023 — Cierres y reportería (estados financieros, cartera, presupuesto)

| Campo | Valor |
|---|---|
| **Código** | HU-0023 |
| **Módulo** | Contabilidad / Reportería |
| **Estado** | En desarrollo (backend) |
| **Prioridad** | Media |
| **Dependencias** | HU-0007 (cuenta/periodo/comprobante/saldo), HU-0010/0011 (factura/cartera), HU-0015 (presupuesto); V8, V12, V15 |

## Historia
> **Como** responsable financiero
> **quiero** cerrar el periodo contable de forma orquestada y consultar los estados financieros, la cartera y la ejecución presupuestal
> **para** validar y reportar la operación del periodo.

## Criterios de aceptación (Gherkin)
```gherkin
Escenario: Cierre orquestado de un periodo
  Dado un periodo contable 'abierto' sin comprobantes en borrador
  Cuando hago POST /api/cierres/periodo/{id}
  Entonces se recalculan los saldos del periodo y el periodo pasa a 'cerrado'

Escenario: No cerrar con borradores
  Dado un periodo con comprobantes en estado 'borrador'
  Cuando intento cerrarlo
  Entonces se rechaza indicando cuántos borradores faltan por confirmar

Escenario: Estados financieros
  Cuando consulto /api/reportes/balance-general y /estado-resultados de un periodo
  Entonces obtengo activo/pasivo/patrimonio (cuadre) e ingresos/costos/gastos (utilidad)
```

## Reglas de negocio
- RN1: La **clase contable** se deriva del primer dígito del código PUC: 1=activo, 2=pasivo, 3=patrimonio,
  4=ingreso, 5/6/7=costo/gasto. Solo cuentas de movimiento (`maneja_movimiento`).
- RN2: **Balance general**: activos (saldo deudor = débito−crédito); pasivos y patrimonio (saldo acreedor =
  crédito−débito). `descuadre = totalActivo − (totalPasivo + totalPatrimonio)`.
- RN3: **Estado de resultados**: ingresos (crédito−débito) − costos/gastos (débito−crédito) = utilidad.
- RN4: **Cierre orquestado** (`/api/cierres/periodo/{id}`): valida que el periodo esté `abierto` y sin
  comprobantes en `borrador`; **recalcula los saldos** (proyección) y **cierra** el periodo. Reutiliza
  `SaldoContableService.recalcular` + `PeriodoContableService.cerrar`.
- RN5: **Cartera** (`/api/reportes/cartera`): saldo de `cuenta_por_cobrar` por cliente (estados
  `vigente`/`en_acuerdo`, `saldo > 0`) con la porción **vencida** (fecha_vencimiento < hoy).
- RN6: **Ejecución presupuestal** (`/api/reportes/ejecucion-presupuestal?vigenciaId=`): por rubro,
  apropiación (plan + adiciones − reducciones + créditos − contra-créditos − aplazamientos), comprometido,
  obligado, pagado y `disponible = apropiación − comprometido`.
- RN7: Todo multitenant por `empresa_id` del JWT. Reportes de solo lectura sobre las **proyecciones**
  (`saldo_contable`, `saldo_presupuestal`); recalcula saldos (o cierra el periodo) para datos al día.

## Contrato de API
| Método | Ruta | Request |
|---|---|---|
| GET | `/api/reportes/balance-general?periodoContableId=` | — |
| GET | `/api/reportes/estado-resultados?periodoContableId=` | — |
| GET | `/api/reportes/cartera` | — |
| GET | `/api/reportes/ejecucion-presupuestal?vigenciaId=` | — |
| POST | `/api/cierres/periodo/{periodoContableId}` | — |

## Preguntas abiertas
- Cierre de **vigencia** (anual) que arrastre saldos de apertura al siguiente periodo: diferido.
- Estados financieros comparativos (varios periodos) y notas: fuera del alcance de v1.
- Edades de cartera por tramos (30/60/90): v1 reporta corriente vs vencida; los tramos se difieren.
- Hardening (índices/tuning, pruebas de carga, Swagger completo, revisión `ciberseguridad`): tareas de
  operación, no slices de código.
