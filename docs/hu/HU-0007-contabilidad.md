# HU-0007 — Contabilidad básica (plan de cuentas, periodo, comprobante, saldos)

| Campo | Valor |
|---|---|
| **Código** | HU-0007 |
| **Módulo** | Contabilidad |
| **Estado** | En desarrollo |
| **Prioridad** | Alta |
| **Dependencias** | HU-0001 (auth); HU-0006 (tipo_documento); migración V8 (cuenta, periodo_contable, comprobante, movimiento_contable, saldo_contable); V1 (vigencia), V5 (tercero), V7 (centro_costo, proyecto) |

## Historia
> **Como** contador / módulos del ERP
> **quiero** un plan de cuentas, periodos contables y comprobantes con partida doble (asientos)
> **para** registrar la contabilidad como sumidero de interfaces y producir saldos confiables.

## Criterios de aceptación (Gherkin)
```gherkin
Escenario: Crear cuenta del plan contable
  Dado una naturaleza válida (debito|credito)
  Cuando hago POST /api/cuentas
  Entonces se crea con empresa_id del token y código único por empresa

Escenario: Crear comprobante balanceado (partida doble)
  Dado un comprobante con movimientos cuya Σdébito = Σcrédito y ≠ 0
  Cuando hago POST /api/comprobantes
  Entonces se crea en estado 'borrador' con sus movimientos

Escenario: Comprobante descuadrado
  Dado un comprobante con Σdébito ≠ Σcrédito
  Cuando hago POST /api/comprobantes
  Entonces recibo 400 "El comprobante no está balanceado (débito ≠ crédito)"

Escenario: Movimiento en cuenta no imputable
  Dado un movimiento sobre una cuenta con maneja_movimiento=false
  Cuando hago POST /api/comprobantes
  Entonces recibo 400 "La cuenta {código} no admite movimientos"

Escenario: Confirmar comprobante en periodo cerrado
  Dado un periodo en estado 'cerrado'
  Cuando confirmo un comprobante de ese periodo
  Entonces recibo 400 "El periodo contable está cerrado"

Escenario: Reversar comprobante confirmado
  Dado un comprobante confirmado
  Cuando hago POST /api/comprobantes/{id}/reversar
  Entonces el original queda 'reversado' y se crea un comprobante inverso 'confirmado'

Escenario: Recalcular saldos
  Cuando hago POST /api/saldos/recalcular para un periodo
  Entonces saldo_contable se reconstruye desde los movimientos no-borrador del periodo
```

## Reglas de negocio
- RN1: `cuenta.codigo_cuenta` único por empresa; `naturaleza ∈ {debito, credito}`; jerárquica (autoreferencia).
- RN2: Solo cuentas con `maneja_movimiento=true` reciben movimientos.
- RN3: `periodo_contable` único por (empresa, anio, mes); estados `abierto ↔ cerrado`. No se borra (entidad de control).
- RN4: **Partida doble**: en un comprobante Σdébito = Σcrédito y total ≠ 0. Cada movimiento tiene débito XOR crédito (uno en 0).
- RN5: `movimiento_contable` es **append-only** (sin updated_at/deleted_at): no se edita ni borra. Un comprobante en 'borrador' se puede cancelar (soft-delete del encabezado); uno 'confirmado' solo se corrige con **reversa**.
- RN6: Confirmar exige periodo **abierto** y recalcula/valida totales. Estados del comprobante: `borrador → confirmado → reversado`.
- RN7: Reversar genera un comprobante inverso (débito↔crédito) en estado 'confirmado', enlazado por `origen_modulo='contabilidad'`/`origen_id` al original; el original pasa a 'reversado'.
- RN8: `saldo_contable` es **proyección recalculable** (no fuente de verdad): se reconstruye desde los movimientos de comprobantes `estado <> 'borrador'`.
- RN9: Multitenant (`empresa_id` del JWT), cross-tenant = 404. FKs (tercero, centro_costo, proyecto, impuesto) deben ser de la empresa.

## Datos
- **Entidades dueñas:** `cuenta`, `periodo_contable`, `comprobante`, `movimiento_contable`, `saldo_contable` (V8).
- **Migración nueva requerida:** no.

## Contrato de API
| Método | Ruta | Request | Response |
|---|---|---|---|
| POST/PUT/DELETE/GET + /list | `/api/cuentas` | `Create/UpdateCuentaRequestDto` | `ApiResponse<CuentaResponseDto>` |
| POST/GET + /list | `/api/periodos-contables` | `CreatePeriodoContableRequestDto` | `ApiResponse<PeriodoContableResponseDto>` |
| POST | `/api/periodos-contables/{id}/cerrar` · `/reabrir` | — | `ApiResponse<Boolean>` |
| POST | `/api/comprobantes` | `CreateComprobanteRequestDto` (con movimientos) | `ApiResponse<ComprobanteResponseDto>` (borrador) |
| GET | `/api/comprobantes/{id}` | — | `ApiResponse<ComprobanteResponseDto>` (con movimientos) |
| POST | `/api/comprobantes/{id}/confirmar` · `/reversar` | — | `ApiResponse<...>` |
| DELETE | `/api/comprobantes/{id}` | — | `ApiResponse<Boolean>` (solo borrador) |
| POST | `/api/comprobantes/list` | `PageableDto` | `ApiResponse<PageResponse<ComprobanteTableDto>>` |
| POST | `/api/saldos/recalcular` | `{ periodoContableId }` | `ApiResponse<Integer>` (filas) |
| GET | `/api/saldos` | query (periodoContableId, cuentaId?) | `ApiResponse<List<SaldoContableResponseDto>>` |

## Estados / máquina de estados
- Comprobante: `borrador → confirmado → reversado`.
- Periodo: `abierto ↔ cerrado`.

## Eventos de dominio
- **Entrada (sumidero):** la interfaz contable consume `AsientoContableSolicitado` desde el bus de dominio
  in-process y genera un comprobante confirmado. Ver `docs/arquitectura/adr-bus-eventos-dominio.md`.
- **Salida (futuro):** `ComprobanteConfirmado` (para conciliación/reportes).

## Multitenencia y seguridad
- `empresa_id` desde el JWT (TenantContext). En el flujo por evento, el `TenantInfo` se inyecta al Reactor
  Context desde el propio evento (que viaja con `empresaId`/`usuarioId`/`sedeId`).

## Preguntas abiertas
- ¿El cierre de periodo valida que no queden comprobantes en 'borrador'? (por ahora solo cambia el estado).
- ¿Saldos por ejes tercero/centro_costo se recalculan siempre o bajo demanda? (v1: por cuenta; ejes opcionales en la PK).
