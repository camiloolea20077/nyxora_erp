# HU-0014 — Cuentas por Pagar (factura proveedor + obligación de pago + retenciones)

| Campo | Valor |
|---|---|
| **Código** | HU-0014 |
| **Módulo** | Cuentas por Pagar |
| **Estado** | En desarrollo |
| **Prioridad** | Alta |
| **Dependencias** | HU-0002 (tercero proveedor), HU-0003 (impuesto/retención), HU-0007 (cuenta), HU-0013 (egreso); migración V17 |

## Historia
> **Como** auxiliar de cuentas por pagar
> **quiero** recibir las facturas electrónicas del proveedor, registrar sus eventos RADIAN y crear la obligación de pago con sus retenciones
> **para** controlar lo que se debe y pagarlo vía egreso.

## Criterios de aceptación (Gherkin)
```gherkin
Escenario: Recibir factura de proveedor y registrar eventos
  Cuando hago POST /api/facturas-proveedor y POST /api/facturas-proveedor/{id}/eventos
  Entonces se registra la FE (estado 'recibida') y sus eventos RADIAN (acuse/reclamo/aceptación)

Escenario: Crear obligación de pago con retenciones
  Dado un proveedor y un valor total con retenciones
  Cuando hago POST /api/obligaciones-pago
  Entonces se crea 'pendiente' con saldo = valorTotal − Σ retenciones

Escenario: Pago vía egreso
  Cuando giro un egreso (HU-0013) aplicado a la obligación
  Entonces el saldo de la obligación baja (parcial/pagada); al anular el egreso, el saldo se restaura
```

## Reglas de negocio
- RN1: Factura proveedor estados: `recibida → aceptada/rechazada` (vía evento). Editable solo en 'recibida'.
- RN2: Obligación estados: `pendiente → parcial → pagada / anulada`. saldo = valorTotal − Σ retenciones al crear. Σ retenciones ≤ valorTotal.
- RN3: El pago lo realiza `comprobante_egreso` (HU-0013) reduciendo el saldo. Anular obligación solo sin pagos aplicados (saldo == valorTotal).
- RN4: Multitenant `empresa_id` del JWT, cross-tenant=404, soft-delete.

## Datos
- **Entidades dueñas:** `factura_proveedor` (+ `factura_proveedor_evento`), `obligacion_pago` (+ `obligacion_pago_retencion`) (V17).
- **Consumida por:** `comprobante_egreso` (V16) vía origen_modulo='cuentas_por_pagar'.

## Contrato de API
| Método | Ruta | Request | Response |
|---|---|---|---|
| POST/PUT/DELETE/GET + /list | `/api/facturas-proveedor` | `Create/UpdateFacturaProveedorRequestDto` | `ApiResponse<FacturaProveedorResponseDto>` |
| POST | `/api/facturas-proveedor/{id}/eventos` | `RegistrarEventoRequestDto` | `ApiResponse<FacturaProveedorResponseDto>` |
| POST/GET + /list | `/api/obligaciones-pago` | `CreateObligacionPagoRequestDto` (con retenciones) | `ApiResponse<ObligacionPagoResponseDto>` |
| POST | `/api/obligaciones-pago/{id}/anular` | — | `ApiResponse<Boolean>` |

## Preguntas abiertas
- ¿La obligación se crea automáticamente al aceptar la factura del proveedor? (v1: creación manual referenciando `facturaProveedorId`).
- ¿Las retenciones se derivan de config por proveedor/impuesto? (v1: se capturan al crear la obligación).
