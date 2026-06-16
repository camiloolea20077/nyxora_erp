# HU-0017 — Contratación (contrato, modalidad, cláusulas, pólizas)

| Campo | Valor |
|---|---|
| **Código** | HU-0017 |
| **Módulo** | Contratación |
| **Estado** | En desarrollo (backend) |
| **Prioridad** | Media |
| **Dependencias** | HU-0002 (tercero / contratista), HU-0016 (poliza_seguro); migración V19 |

## Historia
> **Como** responsable de contratación
> **quiero** registrar contratos con su modalidad, cláusulas y pólizas
> **para** gestionar la adquisición de bienes y servicios y su ciclo de vida.

## Criterios de aceptación (Gherkin)
```gherkin
Escenario: Catálogos de contratación
  Cuando creo una modalidad de contrato y una plantilla de cláusula
  Entonces quedan disponibles para reutilizar en los contratos

Escenario: Crear contrato con cláusulas
  Cuando creo un contrato con contratista, modalidad, objeto, valor y una lista de cláusulas
  Entonces el contrato queda en estado 'planeado' con sus cláusulas

Escenario: Avanzar el estado y adjuntar póliza
  Dado un contrato registrado
  Cuando hago POST /api/contratos/{id}/estado con un estado válido
  Y POST /api/contratos/{id}/polizas con una póliza de seguro
  Entonces el contrato refleja el nuevo estado y lista la póliza
```

## Reglas de negocio
- RN1: `modalidad_contrato` y `clausula_plantilla` son catálogos maestros (código/tipo, soft-delete).
- RN2: `contrato` arranca en estado `planeado`; transiciones válidas a `adjudicado|suscrito|en_ejecucion|liquidado|anulado` (CHECK en BD). El cambio de estado es explícito vía endpoint.
- RN3: Cláusulas son hijos del contrato (se reemplazan en update; pueden poblarse desde plantillas). Pólizas son sub-recurso link (alta/baja idempotente).
- RN4: Multitenant `empresa_id` del JWT, cross-tenant=404, soft-delete.

## Datos
- **Entidades dueñas:** `modalidad_contrato`, `clausula_plantilla`, `contrato`, `contrato_clausula`, `contrato_poliza` (V19). Reutiliza `poliza_seguro` (V18).

## Contrato de API
| Método | Ruta | Request |
|---|---|---|
| CRUD + /list | `/api/modalidades-contrato` | `Create/UpdateModalidadContratoRequestDto` |
| CRUD + /list | `/api/clausulas-plantilla` | `Create/UpdateClausulaPlantillaRequestDto` |
| CRUD + /list | `/api/contratos` | `Create/UpdateContratoRequestDto` (incluye cláusulas) |
| POST /{id}/estado | `/api/contratos` | `CambiarEstadoContratoRequestDto` |
| POST /{id}/polizas · DELETE /{id}/polizas/{polizaId} | `/api/contratos` | `AsignarPolizaRequestDto` |

## Preguntas abiertas
- Vínculo contrato → orden de compra / compromiso presupuestal: se difiere a la interfaz con Compras/Presupuesto.
- Numeración automática del contrato vía motor de documentos (HU-0006): v1 acepta número manual.
- Actas (inicio, suspensión, liquidación) y modificaciones (otrosí): fuera del alcance de v1.
