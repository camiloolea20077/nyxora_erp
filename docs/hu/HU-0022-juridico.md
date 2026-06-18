# HU-0022 — Jurídico (procesos disciplinarios)

| Campo | Valor |
|---|---|
| **Código** | HU-0022 |
| **Módulo** | Jurídico |
| **Estado** | En desarrollo (backend) |
| **Prioridad** | Media |
| **Dependencias** | HU-0002 (tercero responsable), HU-0019 (vinculación investigado); migración V23 |

## Historia
> **Como** responsable de talento humano / jurídico
> **quiero** registrar procesos disciplinarios con sus faltas, descargos y notificaciones
> **para** llevar el debido proceso de los empleados.

## Criterios de aceptación (Gherkin)
```gherkin
Escenario: Catálogos de faltas
  Cuando creo clasificaciones de falta y faltas (con caducidad/política)
  Entonces quedan disponibles para imputarlas en los procesos

Escenario: Proceso disciplinario con debido proceso
  Cuando creo un proceso (investigado, responsable, fecha)
  Y le imputo faltas, registro descargos y notificaciones
  Entonces el proceso refleja su historial y su estado avanza con POST /{id}/estado
```

## Reglas de negocio
- RN1: `clasificacion_falta` y `falta` (código único) son catálogos por empresa (soft-delete). La falta valida su
  clasificación si se indica.
- RN2: `proceso_disciplinario` arranca en estado `abierto`; el cambio de estado es explícito vía endpoint
  (texto libre: `abierto`, `descargos`, `fallo`, `archivado`, `ejecutoriado`, `anulado`…). El investigado es una
  vinculación; el responsable es un tercero.
- RN3: Satélites del proceso (alta/baja): **faltas** (imputación idempotente, valida la falta), **descargos**
  (fecha/texto) y **notificaciones** (fecha/tipo/texto).
- RN4: Multitenant `empresa_id` del JWT, cross-tenant=404, soft-delete.

## Datos
- **Entidades dueñas:** `clasificacion_falta`, `falta`, `proceso_disciplinario`, `proceso_falta`,
  `proceso_descargo`, `proceso_notificacion` (V23). Reutiliza `vinculacion` (V21) y `tercero` (V5).

## Contrato de API
| Método | Ruta | Request |
|---|---|---|
| CRUD + /list | `/api/clasificaciones-falta` | `Create/UpdateClasificacionFaltaRequestDto` |
| CRUD + /list | `/api/faltas` | `Create/UpdateFaltaRequestDto` |
| CRUD + /list | `/api/procesos-disciplinarios` | `Create/UpdateProcesoDisciplinarioRequestDto` |
| POST /{id}/estado | `/api/procesos-disciplinarios` | `CambiarEstadoProcesoRequestDto` |
| faltas/descargos/notificaciones | `/api/procesos-disciplinarios/{id}/...` | `AddProcesoFaltaDto`, `CreateProcesoDescargoDto`, `CreateProcesoNotificacionDto` |

## Preguntas abiertas
- Sanciones y su interfaz con nómina (suspensión sin sueldo → novedad): diferido.
- Caducidad/prescripción automática a partir de `falta.caducidad_dias`: v1 la guarda pero no la calcula.
- Adjuntos del expediente (pruebas, actas): vía motor de adjuntos (HU-0006), no en este slice.
