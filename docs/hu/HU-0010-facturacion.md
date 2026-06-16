# HU-0010 — Facturación (factura + resolución DIAN → inventario + cartera + contabilidad)

| Campo | Valor |
|---|---|
| **Código** | HU-0010 |
| **Módulo** | Facturación |
| **Estado** | En desarrollo |
| **Prioridad** | Alta |
| **Dependencias** | HU-0002 (tercero cliente), HU-0003 (producto/impuesto), HU-0006 (tipo_documento), HU-0007 (contabilidad + bus de eventos), HU-0009 (bodega/inventario); migración V11; V8/V9 |

## Historia
> **Como** facturador
> **quiero** registrar facturas de venta contra una resolución DIAN
> **para** que al emitirlas se numeren consecutivamente, salga la mercancía de inventario y se
> generen el asiento contable y la cuenta por cobrar.

## Criterios de aceptación (Gherkin)
```gherkin
Escenario: Crear factura
  Dado un cliente de mi empresa y líneas con cantidad/valor
  Cuando hago POST /api/facturas
  Entonces se crea en estado 'borrador' con totales calculados (subtotal/descuento/impuestos/total)

Escenario: Emitir factura → numeración DIAN
  Dado una factura en 'borrador' con resolución DIAN vigente
  Cuando hago POST /api/facturas/{id}/emitir
  Entonces pasa a 'emitida' y recibe número consecutivo (prefijo + consecutivo) tomado atómicamente
    de la resolución (FOR UPDATE), validando que esté dentro del rango factura_inicial..factura_final

Escenario: Emitir factura → salida de inventario
  Dado una factura en 'borrador' con bodega y líneas de productos que manejan inventario
  Cuando emito la factura
  Entonces por cada línea se genera un movimiento de inventario 'salida' (cantidad negativa,
    origen facturacion, origen_id=factura) en la bodega de la línea o de la factura

Escenario: Emitir factura → contabilidad (opcional)
  Dado que envío cuentaClienteId, cuentaIngresoId (y cuentaImpuestoId si hay IVA) y periodoContableId
  Cuando emito la factura
  Entonces se publica AsientoContableSolicitado (débito CxC cliente / crédito ingreso + crédito IVA)
    y la interfaz contable genera el comprobante

Escenario: Emitir factura → cartera
  Cuando emito la factura
  Entonces se publica CuentaPorCobrarSolicitada para que Cartera (Sprint 8) registre la CxC

Escenario: Anular factura emitida
  Dado una factura 'emitida' con salidas de inventario
  Cuando hago POST /api/facturas/{id}/anular
  Entonces pasa a 'anulada' y se generan movimientos de inventario 'entrada' que reversan las salidas

Escenario: Registrar factura electrónica (DIAN)
  Dado una factura 'emitida'
  Cuando hago POST /api/facturas/{id}/dian con cufe/estadoDian
  Entonces se guarda/actualiza la metadata de la FE (factura_dian)
```

## Reglas de negocio
- RN1: Factura estados: `borrador → emitida → anulada`; se anula desde `borrador` o `emitida`.
- RN2: Totales de la factura = Σ líneas; por línea: subtotal = cantidad·valor_unitario − descuento; total = subtotal + impuesto.
- RN3: La numeración consecutiva sale de la **resolución DIAN** (atómica, `FOR UPDATE`); el número resultante (prefijo+consecutivo) debe caer en el rango `factura_inicial..factura_final` de la resolución.
- RN4: Emitir genera **movimiento_inventario** tipo `salida` (cantidad negativa, append-only, origen_modulo='facturacion', origen_id=factura) por cada línea con bodega; todo en una transacción.
- RN5: Si se proveen cuentas + periodo, al emitir se publica `AsientoContableSolicitado` (débito CxC cliente / crédito ingreso + crédito IVA). Sin cuentas → no hay asiento.
- RN6: Al emitir se publica `CuentaPorCobrarSolicitada` (consumido por Cartera en Sprint 8; hoy best-effort sin listener).
- RN7: Anular una factura `emitida` reversa el inventario con movimientos `entrada` compensatorios. La reversa contable (nota crédito) se difiere.
- RN8: Factura editable/eliminable solo en `borrador`. Multitenant `empresa_id` del JWT, cross-tenant=404.

## Datos
- **Entidades dueñas:** `resolucion_dian`, `factura`, `factura_linea`, `factura_dian` (V11).
- **Consume:** `movimiento_inventario` (V9), bus de eventos → `comprobante` (V8) y Cartera (V12, Sprint 8).
- **Migración nueva requerida:** no.

## Contrato de API
| Método | Ruta | Request | Response |
|---|---|---|---|
| POST/PUT/DELETE/GET + /list | `/api/resoluciones-dian` | `Create/UpdateResolucionDianRequestDto` | `ApiResponse<ResolucionDianResponseDto>` |
| POST/PUT/DELETE/GET + /list | `/api/facturas` | `Create/UpdateFacturaRequestDto` (con líneas) | `ApiResponse<FacturaResponseDto>` |
| POST | `/api/facturas/{id}/emitir` | `EmitirFacturaRequestDto` (cuentas/periodo opcionales) | `ApiResponse<FacturaResponseDto>` |
| POST | `/api/facturas/{id}/anular` | — | `ApiResponse<Boolean>` |
| POST | `/api/facturas/{id}/dian` | `RegistrarFacturaDianRequestDto` | `ApiResponse<FacturaDianResponseDto>` |

## Eventos de dominio
- **Publica:** `AsientoContableSolicitado` (al emitir con cuentas) → interfaz contable (HU-0007).
- **Publica:** `CuentaPorCobrarSolicitada` (al emitir) → Cartera (Sprint 8).
- **Genera (llamada directa):** salidas en `movimiento_inventario` (HU-0009).

## Multitenencia y seguridad
- `empresa_id` del JWT (TenantContext). Cross-tenant = 404. Las líneas se cargan por la factura (no se exponen ids de otra empresa).

## Preguntas abiertas
- ¿Se debe bloquear la emisión por falta de stock? (v1: no se bloquea, consistente con el módulo de inventario que permite salida sin saldo; se difiere validación dura).
- ¿La cuenta de ingreso/IVA/CxC debería derivarse de config por producto/impuesto/cliente? (v1: se pasan al emitir; config contable se difiere).
- ¿La numeración sin resolución DIAN usa el consecutivo de tipo_documento? (v1: si no hay resolución se respeta el `numero` enviado manualmente).
