# HU-XXXX — <Título corto>

| Campo | Valor |
|---|---|
| **Código** | HU-XXXX |
| **Módulo** | Administración / Común / Compras / Inventario / Facturación / Caja / Cartera / Contabilidad |
| **Estado** | Propuesta / En análisis / Lista para desarrollo / En desarrollo / Hecha |
| **Prioridad** | Alta / Media / Baja |
| **Dependencias** | HU-… / migración V… / agente … |

## Historia
> **Como** <rol>
> **quiero** <capacidad>
> **para** <beneficio>.

## Criterios de aceptación (Gherkin)
```gherkin
Escenario: <nombre>
  Dado <contexto / precondición>
  Cuando <acción>
  Entonces <resultado esperado>
```

## Reglas de negocio
- RN1: …
- RN2: …

## Datos (consultar agente base-datos)
- **Entidad(es) dueña(s):** …
- **Tablas/columnas implicadas:** … (ver `.claude/data/diccionario-datos.md`)
- **Migración nueva requerida:** sí/no → V…

## Contrato de API
| Método | Ruta | Request | Response |
|---|---|---|---|
| POST | `/api/…` | `Create…RequestDto` | `ApiResponse<…ResponseDto>` |

## Estados / máquina de estados
`estado_a → estado_b → …`

## Eventos de dominio
- `…Ocurrido` → consumido por <módulo>.

## Multitenencia y seguridad
- `empresa_id` desde el JWT (TenantContext). Permisos requeridos: `modulo.entidad.accion`.

## Preguntas abiertas
- …
