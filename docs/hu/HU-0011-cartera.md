# HU-0011 — Cartera (cuentas por cobrar + acuerdos de pago)

| Campo | Valor |
|---|---|
| **Código** | HU-0011 |
| **Módulo** | Cartera |
| **Estado** | En desarrollo |
| **Prioridad** | Alta |
| **Dependencias** | HU-0002 (tercero cliente), HU-0007 (cuenta), HU-0010 (factura + evento `CuentaPorCobrarSolicitada`); migración V12 |

## Historia
> **Como** analista de cartera
> **quiero** que las facturas emitidas generen automáticamente su cuenta por cobrar y poder pactar acuerdos de pago
> **para** controlar el saldo del cliente y su recaudo.

## Criterios de aceptación (Gherkin)
```gherkin
Escenario: CxC automática desde factura
  Dado que se emite una factura (HU-0010)
  Cuando se publica el evento CuentaPorCobrarSolicitada
  Entonces la interfaz de cartera crea una cuenta por cobrar 'vigente' con saldo = total de la factura

Escenario: CxC manual (saldo inicial)
  Cuando hago POST /api/cuentas-cobrar con cliente y valor
  Entonces se crea una CxC 'vigente' con saldo = valorTotal

Escenario: Acuerdo de pago
  Dado una CxC 'vigente'
  Cuando hago POST /api/acuerdos-pago con sus cuotas
  Entonces se crea el acuerdo 'vigente' con sus cuotas y la CxC pasa a 'en_acuerdo'
    (la suma de cuotas no puede superar el saldo)

Escenario: Anular acuerdo
  Cuando hago POST /api/acuerdos-pago/{id}/anular
  Entonces el acuerdo pasa a 'anulado' y la CxC vuelve a 'vigente'
```

## Reglas de negocio
- RN1: CxC estados: `vigente → en_acuerdo → pagada / anulada`. El recaudo (Caja, HU-0012) reduce el `saldo`; al llegar a 0 pasa a `pagada`.
- RN2: La CxC desde factura la crea el **listener** `InterfazCarteraListener` (consistencia eventual, best-effort; el tenant viaja en el evento).
- RN3: Un acuerdo solo se pacta sobre una CxC `vigente`; Σ cuotas ≤ saldo. Crear acuerdo → CxC `en_acuerdo`. Anular acuerdo → CxC `vigente`.
- RN4: Multitenant `empresa_id` del JWT, cross-tenant=404. Las edades de cartera son **proyección** (no se almacenan).

## Datos
- **Entidades dueñas:** `cuenta_por_cobrar`, `acuerdo_pago`, `acuerdo_pago_cuota` (V12).
- **Consume:** evento `CuentaPorCobrarSolicitada` (HU-0010).

## Contrato de API
| Método | Ruta | Request | Response |
|---|---|---|---|
| POST/GET + /list | `/api/cuentas-cobrar` | `CreateCuentaPorCobrarRequestDto` | `ApiResponse<CuentaPorCobrarResponseDto>` |
| POST/GET + /list | `/api/acuerdos-pago` | `CreateAcuerdoPagoRequestDto` (con cuotas) | `ApiResponse<AcuerdoPagoResponseDto>` |
| POST | `/api/acuerdos-pago/{id}/anular` | — | `ApiResponse<Boolean>` |

## Eventos de dominio
- **Consume:** `CuentaPorCobrarSolicitada` → crea la CxC.

## Preguntas abiertas
- ¿Liquidación de intereses de mora? (v1: `valor_interes` queda en 0; cálculo de mora se difiere).
- ¿Las cuotas del acuerdo se marcan pagadas al recaudar? (v1: el recibo reduce el saldo de la CxC; el seguimiento cuota-a-cuota se difiere).
