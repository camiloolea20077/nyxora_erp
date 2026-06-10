# HU-0006 — Motor de documentos (tipo_documento, consecutivo atómico, adjuntos)

| Campo | Valor |
|---|---|
| **Código** | HU-0006 |
| **Módulo** | Común (soporte transversal) |
| **Estado** | En desarrollo |
| **Prioridad** | Alta |
| **Dependencias** | HU-0001 (auth); migración V2 (tipo_documento, consecutivo), V25 (adjunto); V1 (sede, vigencia) |

## Historia
> **Como** cualquier módulo transaccional (compras, facturación, caja…)
> **quiero** un motor central de tipos de documento con numeración consecutiva atómica y adjuntos polimórficos
> **para** garantizar consecutivos únicos y sin saltos por (tipo, sede, vigencia) y poder anexar archivos a cualquier registro.

## Criterios de aceptación (Gherkin)
```gherkin
Escenario: Crear tipo de documento
  Dado un tipo con código no registrado en mi empresa
  Cuando hago POST /api/tipos-documento
  Entonces se crea con activo=true y empresa_id de mi token, y recibo 201

Escenario: Obtener consecutivo (atómico)
  Dado un tipo de documento, sede y vigencia de mi empresa
  Cuando solicito el siguiente consecutivo
  Entonces se incrementa ultimo_numero bajo bloqueo (SELECT ... FOR UPDATE) y recibo el número formateado (prefijo+número)

Escenario: Concurrencia
  Dadas N solicitudes simultáneas del mismo consecutivo
  Entonces no se entregan números duplicados ni se saltan números

Escenario: Adjuntar archivo a una entidad
  Cuando hago POST /api/adjuntos con (modulo, entidad, entidadId, url)
  Entonces se registra la referencia del adjunto para mi empresa

Escenario: Listar adjuntos de una entidad
  Cuando hago GET /api/adjuntos?modulo=&entidad=&entidadId=
  Entonces recibo solo los adjuntos vigentes de mi empresa para ese objeto
```

## Reglas de negocio
- RN1: `tipo_documento.codigo` único por empresa; `modulo` obligatorio.
- RN2: `consecutivo` único por (tipo_documento_id, sede_id, vigencia_id); `ultimo_numero ≥ 0`.
- RN3: El siguiente número se obtiene **dentro de una transacción** con `SELECT … FOR UPDATE`; si no existe la fila de consecutivo se crea en 0 y se incrementa.
- RN4: Si `reinicia_por_vigencia=true`, el conteo es independiente por vigencia (ya garantizado por la PK lógica de consecutivo).
- RN5: El número formateado = `prefijo` + número (relleno opcional). Sede/vigencia provienen del JWT/contexto, no del body cuando aplique.
- RN6: `adjunto` es polimórfico por (modulo, entidad, entidad_id); sin FK cruzada. El binario vive en almacenamiento externo; aquí solo la referencia (url).
- RN7: Soft-delete; `empresa_id`/`usuario_id` del JWT; cross-tenant = 404.

## Datos
- **Entidades dueñas:** `tipo_documento`, `consecutivo` (V2), `adjunto` (V25).
- **Migración nueva requerida:** no.

## Contrato de API
| Método | Ruta | Request | Response |
|---|---|---|---|
| POST/PUT/DELETE/GET + /list | `/api/tipos-documento` | `Create/UpdateTipoDocumentoRequestDto` | `ApiResponse<TipoDocumentoResponseDto>` |
| POST | `/api/tipos-documento/{id}/consecutivo` | `ConsecutivoRequestDto` (vigenciaId; sede del JWT) | `ApiResponse<ConsecutivoResponseDto>` (numero, numeroFormateado) |
| POST/DELETE | `/api/adjuntos` | `CreateAdjuntoRequestDto` | `ApiResponse<AdjuntoResponseDto>` |
| GET | `/api/adjuntos` | query (modulo, entidad, entidadId) | `ApiResponse<List<AdjuntoResponseDto>>` |

## Concurrencia / atomicidad
`TransactionalOperator` (R2DBC) envolviendo: `SELECT ultimo_numero FROM consecutivo WHERE … FOR UPDATE` → `UPDATE … SET ultimo_numero = ultimo_numero + 1`.

## Multitenencia y seguridad
- `empresa_id` desde el JWT (TenantContext). Cross-tenant = 404.

## Preguntas abiertas
- ¿El relleno de ceros del número formateado es configurable por tipo_documento? (por ahora sin relleno fijo).
