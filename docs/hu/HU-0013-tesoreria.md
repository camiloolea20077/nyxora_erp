# HU-0013 — Tesorería (cuentas bancarias, chequeras, egresos)

| Campo | Valor |
|---|---|
| **Código** | HU-0013 |
| **Módulo** | Tesorería |
| **Estado** | En desarrollo |
| **Prioridad** | Alta |
| **Dependencias** | HU-0002 (tercero), HU-0004 catálogos (banco, tipo_cuenta_bancaria, forma_pago), HU-0007 (cuenta + bus de eventos), HU-0014 (obligación de pago); migración V16 |

## Historia
> **Como** tesorero
> **quiero** administrar las cuentas bancarias y chequeras de la empresa y girar comprobantes de egreso
> **para** pagar a proveedores/obligaciones y llevar el control del banco.

## Criterios de aceptación (Gherkin)
```gherkin
Escenario: CRUD de cuenta bancaria / chequera
  Cuando hago POST/PUT/DELETE/GET en /api/cuentas-bancarias y /api/chequeras
  Entonces se administran con número de cuenta único por (empresa, banco)

Escenario: Crear y girar un egreso
  Dado un egreso en 'borrador' (opcionalmente aplicado a una obligación de pago)
  Cuando hago POST /api/egresos/{id}/girar
  Entonces pasa a 'girado'; si aplica a una obligación, reduce su saldo (parcial/pagada)
    y si envío cuentaBancoId+cuentaCxpId+periodoContableId se publica el asiento (débito CxP / crédito banco)

Escenario: Anular egreso girado
  Cuando hago POST /api/egresos/{id}/anular
  Entonces pasa a 'anulado' y reversa la aplicación a la obligación (devuelve el saldo)
```

## Reglas de negocio
- RN1: Egreso estados: `borrador → girado → (conciliado) → anulado`; editable/eliminable solo en borrador; se anula desde borrador/girado.
- RN2: Girar contra una obligación valida valor ≤ saldo; saldo 0 → obligación `pagada`, si parcial → `parcial`. Todo transaccional.
- RN3: Asiento contable opcional al girar (consistencia eventual vía `AsientoContableSolicitado`).
- RN4: `cuenta_bancaria` es la cuenta PROPIA de la empresa (distinta de la del tercero). Multitenant `empresa_id` del JWT, cross-tenant=404, soft-delete.

## Datos
- **Entidades dueñas:** `cuenta_bancaria`, `chequera`, `comprobante_egreso` (V16). Diferidos: `extracto_bancario(+detalle)`, `conciliacion_bancaria`.
- **Consume:** `obligacion_pago` (V17) vía origen_modulo='cuentas_por_pagar'; bus de eventos → `comprobante` (V8).

## Contrato de API
| Método | Ruta | Request | Response |
|---|---|---|---|
| POST/PUT/DELETE/GET + /list | `/api/cuentas-bancarias` | `Create/UpdateCuentaBancariaRequestDto` | `ApiResponse<CuentaBancariaResponseDto>` |
| POST/PUT/DELETE/GET + /list | `/api/chequeras` | `Create/UpdateChequeraRequestDto` | `ApiResponse<ChequeraResponseDto>` |
| POST/PUT/DELETE/GET + /list | `/api/egresos` | `Create/UpdateComprobanteEgresoRequestDto` | `ApiResponse<ComprobanteEgresoResponseDto>` |
| POST | `/api/egresos/{id}/girar` · `/anular` | `GirarEgresoRequestDto` (girar) | `ApiResponse<...>` |

## Eventos de dominio
- **Publica:** `AsientoContableSolicitado` (al girar con cuentas) → interfaz contable (HU-0007).
- **Genera (llamada directa):** reduce el saldo de `obligacion_pago` (HU-0014).

## Preguntas abiertas
- Extractos y conciliación bancaria: diferidos (entidades creadas, slice pendiente).
- ¿La chequera consume su consecutivo al girar un cheque? (v1: el consecutivo se administra manualmente; no se enlaza aún al egreso).
