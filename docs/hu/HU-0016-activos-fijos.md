# HU-0016 — Activos Fijos (activo, depreciación, responsables, pólizas)

| Campo | Valor |
|---|---|
| **Código** | HU-0016 |
| **Módulo** | Activos Fijos |
| **Estado** | En desarrollo (backend) |
| **Prioridad** | Media |
| **Dependencias** | HU-0002 (tercero), HU-0003 (producto, marca, unidad_medida), HU-0004 (centro_costo), HU-0009 (bodega); migración V18 |

## Historia
> **Como** responsable de activos fijos
> **quiero** registrar los activos de la empresa, asignarles responsables y pólizas, y aplicar su depreciación
> **para** controlar el valor en libros y la custodia de los bienes.

## Criterios de aceptación (Gherkin)
```gherkin
Escenario: Registrar activo fijo
  Cuando creo un activo fijo con código único, nombre, valor de compra y vida útil
  Entonces queda registrado con estado_activo y valor_actual = valor_compra

Escenario: Asignar responsable y póliza
  Dado un activo fijo registrado
  Cuando hago POST /api/activos-fijos/{id}/responsables con un tercero
  Y POST /api/activos-fijos/{id}/polizas con una póliza de seguro
  Entonces el activo lista ese responsable y esa póliza

Escenario: Depreciar (append-only)
  Dado un activo con vida útil > 0
  Cuando hago POST /api/depreciaciones con activoFijoId, fecha y valor
  Entonces se agrega un detalle de depreciación inmutable
  Y el activo recalcula valor_depreciacion (acumulado), meses_depreciados y valor_actual
```

## Reglas de negocio
- RN1: `activo_fijo` con código único por empresa; `valor_actual` inicia en `valor_compra` y se reduce por depreciación acumulada.
- RN2: `depreciacion` es **append-only** (sólo create + consulta). No se edita ni borra; las correcciones son nuevas depreciaciones (positivas o negativas). Al registrar, se recalcula el acumulado en el activo: `valor_depreciacion = Σ depreciaciones`, `meses_depreciados++`, `valor_actual = valor_compra − valor_depreciacion − deterioro`.
- RN3: Responsables y pólizas son sub-recursos del activo (link). Alta/baja idempotente; baja = soft-delete del vínculo.
- RN4: `poliza_seguro` es maestro propio (reutilizado también por Contratación, HU-0017). Número único por empresa.
- RN5: Multitenant `empresa_id` del JWT, cross-tenant=404, soft-delete en maestros y vínculos.

## Datos
- **Entidades dueñas:** `poliza_seguro`, `activo_fijo`, `depreciacion` (append-only), `activo_fijo_responsable`, `activo_fijo_poliza` (V18).

## Contrato de API
| Método | Ruta | Request |
|---|---|---|
| CRUD + /list | `/api/polizas-seguro` | `Create/UpdatePolizaSeguroRequestDto` |
| CRUD + /list | `/api/activos-fijos` | `Create/UpdateActivoFijoRequestDto` |
| POST /{id}/responsables · DELETE /{id}/responsables/{terceroId} | `/api/activos-fijos` | `AsignarResponsableRequestDto` |
| POST /{id}/polizas · DELETE /{id}/polizas/{polizaId} | `/api/activos-fijos` | `AsignarPolizaRequestDto` |
| POST · GET /{id} · POST /activo/{activoFijoId}/list | `/api/depreciaciones` | `CreateDepreciacionRequestDto` |

## Preguntas abiertas
- Generación automática de la cuota de depreciación (línea recta / saldos decrecientes) a partir de vida útil: v1 registra el valor enviado; el cálculo automático se difiere.
- Asiento contable de la depreciación (gasto vs. depreciación acumulada): se difiere a la interfaz contable.
- Baja/retiro del activo (estado_activo, fecha_salida_servicio) y su reverso: v1 sólo expone el campo en el maestro.
