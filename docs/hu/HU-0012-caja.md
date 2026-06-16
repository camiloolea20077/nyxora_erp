# HU-0012 — Caja (recibo de caja + aplicación a cartera + arqueo)

| Campo | Valor |
|---|---|
| **Código** | HU-0012 |
| **Módulo** | Caja |
| **Estado** | En desarrollo |
| **Prioridad** | Alta |
| **Dependencias** | HU-0001 (sede/usuario), HU-0004 catálogos (forma_pago, banco), HU-0011 (cuenta por cobrar), HU-0007 (contabilidad + bus de eventos); migración V13 |

## Historia
> **Como** cajero
> **quiero** abrir mi caja, registrar recibos de recaudo aplicándolos a las cuentas por cobrar y cuadrarla con un arqueo
> **para** controlar el efectivo del punto de venta.

## Criterios de aceptación (Gherkin)
```gherkin
Escenario: Abrir/cerrar caja
  Cuando hago POST /api/cajas/{id}/abrir con saldo inicial
  Entonces la caja pasa a 'abierta'; al POST /api/cajas/{id}/cerrar vuelve a 'cerrada'

Escenario: Recibo de caja con aplicación a cartera
  Dado una caja 'abierta' y CxC del cliente
  Cuando hago POST /api/recibos-caja con medios de pago y líneas de aplicación
  Entonces se registra el recibo (valor = Σ pagos), por cada línea se reduce el saldo de la CxC
    (si llega a 0 → 'pagada'); la aplicación no puede superar el valor del recibo

Escenario: Recibo → contabilidad (opcional)
  Dado que envío cuentaCajaId, cuentaCxcId y periodoContableId
  Cuando creo el recibo
  Entonces se publica AsientoContableSolicitado (débito caja / crédito CxC)

Escenario: Anular recibo
  Cuando hago POST /api/recibos-caja/{id}/anular
  Entonces el recibo pasa a 'anulado' y se reversa la aplicación (devuelve el saldo a cada CxC)

Escenario: Arqueo
  Cuando hago POST /api/arqueos con la caja y el valor declarado
  Entonces se registra el arqueo con valor_sistema = saldo inicial + Σ recibos registrados y la diferencia
```

## Reglas de negocio
- RN1: Caja estados: `cerrada ↔ abierta`. Solo se recauda con caja `abierta`. No se elimina una caja abierta.
- RN2: Recibo estados: `registrado → anulado`. `valor` = Σ medios de pago. La aplicación a CxC (Σ `valorAplicado`) ≤ `valor`.
- RN3: Aplicar reduce `saldo` de la CxC (cada `valorAplicado` ≤ saldo); saldo 0 → CxC `pagada`. Anular reversa (restaura saldo; CxC `pagada` → `vigente`). Todo transaccional.
- RN4: Si se proveen cuentas + periodo, el recibo publica `AsientoContableSolicitado` (débito caja / crédito CxC).
- RN5: Arqueo append-only: `valor_sistema` = saldo inicial de la caja + Σ recibos `registrado`; `diferencia` = declarado − sistema.
- RN6: Multitenant `empresa_id` del JWT, cross-tenant=404.

## Datos
- **Entidades dueñas:** `caja`, `recibo_caja`, `recibo_caja_pago`, `recibo_caja_linea`, `arqueo` (V13).
- **Consume:** `cuenta_por_cobrar` (V12), bus de eventos → `comprobante` (V8).

## Contrato de API
| Método | Ruta | Request | Response |
|---|---|---|---|
| POST/PUT/DELETE/GET + /list | `/api/cajas` | `Create/UpdateCajaRequestDto` | `ApiResponse<CajaResponseDto>` |
| POST | `/api/cajas/{id}/abrir` · `/cerrar` | `AbrirCajaRequestDto` (abrir) | `ApiResponse<CajaResponseDto>` |
| POST/GET + /list | `/api/recibos-caja` | `CreateReciboCajaRequestDto` (pagos + líneas) | `ApiResponse<ReciboCajaResponseDto>` |
| POST | `/api/recibos-caja/{id}/anular` | — | `ApiResponse<Boolean>` |
| POST/GET + /list | `/api/arqueos` | `CreateArqueoRequestDto` | `ApiResponse<ArqueoResponseDto>` |

## Eventos de dominio
- **Publica:** `AsientoContableSolicitado` (al crear recibo con cuentas) → interfaz contable (HU-0007).
- **Genera (llamada directa):** reduce el saldo de `cuenta_por_cobrar` (HU-0011).

## Preguntas abiertas
- ¿El arqueo debe cerrar la caja automáticamente? (v1: arqueo y cierre son acciones independientes).
- ¿Validar que la suma de pagos sea exacta al total aplicado? (v1: se permite recaudo con saldo a favor / sin aplicar; Σ aplicación ≤ valor).
