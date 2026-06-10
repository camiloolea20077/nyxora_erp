# HU-0008 — Compras (orden de compra + recepción → inventario + contabilidad)

| Campo | Valor |
|---|---|
| **Código** | HU-0008 |
| **Módulo** | Compras |
| **Estado** | En desarrollo |
| **Prioridad** | Alta |
| **Dependencias** | HU-0003 (producto), HU-0009 (bodega/inventario), HU-0007 (contabilidad + bus de eventos); migración V10; V5 (tercero proveedor) |

## Historia
> **Como** comprador
> **quiero** registrar órdenes de compra y sus recepciones
> **para** que al recibir mercancía entre automáticamente a inventario y se genere el asiento contable.

## Criterios de aceptación (Gherkin)
```gherkin
Escenario: Crear orden de compra
  Dado un proveedor de mi empresa y líneas con cantidad/valor
  Cuando hago POST /api/ordenes-compra
  Entonces se crea en estado 'borrador' con totales calculados (subtotal/impuestos/total)

Escenario: Aprobar orden
  Dado una orden en 'borrador'
  Cuando hago POST /api/ordenes-compra/{id}/aprobar
  Entonces pasa a 'aprobada' (habilitada para recepción)

Escenario: Confirmar recepción → inventario
  Dado una orden 'aprobada' y una recepción 'borrador' con cantidades ≤ pendiente
  Cuando hago POST /api/recepciones/{id}/confirmar
  Entonces por cada línea se genera un movimiento de inventario 'entrada' (origen compras),
    se actualiza cantidad_recibida de la orden y su estado (recibida_parcial/total)

Escenario: Confirmar recepción → contabilidad (opcional)
  Dado que envío cuentaInventarioId, cuentaContrapartidaId y periodoContableId
  Cuando confirmo la recepción
  Entonces se publica AsientoContableSolicitado (débito inventario / crédito contrapartida)
    y la interfaz contable genera el comprobante

Escenario: Recepción mayor a lo pendiente
  Cuando confirmo una recepción cuya cantidad supera lo pendiente de la línea
  Entonces recibo 400 "La cantidad recibida supera lo pendiente"
```

## Reglas de negocio
- RN1: Orden estados: `borrador → aprobada → recibida_parcial → recibida_total → cerrada`; `anulada` desde borrador/aprobada.
- RN2: Solo se recibe contra órdenes `aprobada`/`recibida_parcial`.
- RN3: Totales de la orden = Σ líneas; por línea: subtotal = cantidad·valor_unitario − descuento; total = subtotal + impuesto.
- RN4: `cantidad_recibida` por línea ≤ `cantidad`; `cantidad_pendiente = cantidad − cantidad_recibida`.
- RN5: Confirmar recepción genera **movimiento_inventario** tipo `entrada` (append-only, origen_modulo='compras', origen_id=recepcion) en la bodega de la recepción; todo en una transacción.
- RN6: Si se proveen cuentas + periodo, se publica `AsientoContableSolicitado` (consistencia eventual vía bus; la interfaz contable crea el comprobante). Sin cuentas → solo inventario.
- RN7: Orden/recepción editable/eliminable solo en `borrador`. Multitenant `empresa_id` del JWT, cross-tenant=404.

## Datos
- **Entidades dueñas:** `orden_compra`, `orden_compra_linea`, `recepcion`, `recepcion_linea` (V10).
- **Consume:** `movimiento_inventario` (V9), bus de eventos → `comprobante` (V8).
- **Migración nueva requerida:** no.

## Contrato de API
| Método | Ruta | Request | Response |
|---|---|---|---|
| POST/PUT/DELETE/GET + /list | `/api/ordenes-compra` | `Create/UpdateOrdenCompraRequestDto` (con líneas) | `ApiResponse<OrdenCompraResponseDto>` |
| POST | `/api/ordenes-compra/{id}/aprobar` · `/anular` | — | `ApiResponse<Boolean>` |
| POST/GET + /list | `/api/recepciones` | `CreateRecepcionRequestDto` (con líneas) | `ApiResponse<RecepcionResponseDto>` |
| POST | `/api/recepciones/{id}/confirmar` | `ConfirmarRecepcionRequestDto` (cuentas/periodo opcionales) | `ApiResponse<RecepcionResponseDto>` |

## Eventos de dominio
- **Publica:** `AsientoContableSolicitado` (al confirmar recepción con cuentas) → interfaz contable (HU-0007).
- **Genera (llamada directa):** entradas en `movimiento_inventario` (HU-0009).

## Multitenencia y seguridad
- `empresa_id` del JWT (TenantContext). Cross-tenant = 404.

## Preguntas abiertas
- ¿La cuenta de inventario/contrapartida debería derivarse de config por producto/categoría? (v1: se pasan al confirmar; config contable se difiere).
- ¿Cierre de orden (`cerrada`) manual o automático al recibir todo? (v1: automático `recibida_total`; `cerrada` manual futuro).
