# HU-0009 — Inventario (bodegas, ubicaciones, lotes, marcas, movimientos, saldos/kardex)

| Campo | Valor |
|---|---|
| **Código** | HU-0009 |
| **Módulo** | Inventario |
| **Estado** | En desarrollo |
| **Prioridad** | Alta |
| **Dependencias** | HU-0001 (auth); HU-0003 (producto/variante); migración V9; V1 (sede), V2 (impuesto), V5 (tercero), V7 (centro_costo) |

## Historia
> **Como** responsable de inventario / módulos del ERP
> **quiero** bodegas, ubicaciones, lotes y movimientos de inventario con saldos y kardex
> **para** controlar existencias y costos por bodega como sumidero de compras/facturación.

## Criterios de aceptación (Gherkin)
```gherkin
Escenario: Crear bodega
  Cuando hago POST /api/bodegas con código único
  Entonces se crea con empresa_id del token

Escenario: Registrar entrada de inventario
  Dado un producto y una bodega de mi empresa
  Cuando hago POST /api/movimientos-inventario tipo 'entrada' cantidad>0
  Entonces se registra un movimiento append-only que SUMA al saldo

Escenario: Registrar salida
  Cuando hago POST /api/movimientos-inventario tipo 'salida' cantidad>0
  Entonces se registra un movimiento que RESTA al saldo (cantidad almacenada negativa)

Escenario: Traslado entre bodegas
  Cuando hago POST /api/movimientos-inventario/traslado origen→destino
  Entonces se crean DOS movimientos (origen − / destino +) en una transacción

Escenario: Reversar un movimiento
  Cuando hago POST /api/movimientos-inventario/{id}/reversar
  Entonces se crea un movimiento inverso (no se edita el original)

Escenario: Recalcular saldos
  Cuando hago POST /api/saldos-inventario/recalcular para una bodega
  Entonces saldo_inventario se reconstruye (cantidad neta, costo promedio, valor) desde los movimientos

Escenario: Kardex
  Cuando hago GET /api/movimientos-inventario/kardex?productoId=&bodegaId=
  Entonces recibo los movimientos ordenados con su saldo corriente
```

## Reglas de negocio
- RN1: `codigo` único por empresa en marca/bodega/lote; `ubicacion.codigo` único por bodega.
- RN2: `movimiento_inventario` es **append-only** (sin updated_at/deleted_at): no se edita ni borra; las correcciones son movimientos de reversa.
- RN3: **`cantidad` se almacena con signo** (positivo suma, negativo resta). El `tipo ∈ {entrada, salida, ajuste, traslado}` es descriptivo. entrada⇒+, salida⇒−, ajuste⇒signo libre (≠0), traslado⇒2 filas (origen − / destino +).
- RN4: El producto/variante/bodega/ubicacion/lote/impuesto/centro_costo/tercero referenciados deben ser de la empresa.
- RN5: `saldo_inventario` es **proyección recalculable** (no fuente de verdad): cantidad neta = SUM(cantidad); costo_promedio = Σ(cantidad·costo de entradas)/Σ(cantidad de entradas); valor_total = cantidad·costo_promedio.
- RN6: ubicacion es jerárquica (autoreferencia) dentro de su bodega.
- RN7: Catálogos (marca/bodega/ubicacion/lote) usan soft-delete; multitenant `empresa_id` del JWT; cross-tenant = 404.

## Datos
- **Entidades dueñas:** `marca`, `bodega`, `bodega_responsable`, `ubicacion`, `lote`, `movimiento_inventario`, `saldo_inventario` (V9).
- **Migración nueva requerida:** no.

## Contrato de API
| Método | Ruta | Request | Response |
|---|---|---|---|
| CRUD + /list | `/api/marcas` | `Create/UpdateMarcaRequestDto` | `ApiResponse<MarcaResponseDto>` |
| CRUD + /list | `/api/bodegas` | `Create/UpdateBodegaRequestDto` | `ApiResponse<BodegaResponseDto>` |
| GET/POST/PUT/DELETE | `/api/bodegas/{bodegaId}/responsables` | satélite | `ApiResponse<...>` |
| CRUD + /list | `/api/ubicaciones` | `Create/UpdateUbicacionRequestDto` | `ApiResponse<UbicacionResponseDto>` |
| CRUD + /list | `/api/lotes` | `Create/UpdateLoteRequestDto` | `ApiResponse<LoteResponseDto>` |
| POST | `/api/movimientos-inventario` | `CreateMovimientoInventarioDto` | `ApiResponse<MovimientoInventarioResponseDto>` |
| POST | `/api/movimientos-inventario/traslado` | `TrasladoInventarioDto` | `ApiResponse<...>` |
| POST | `/api/movimientos-inventario/{id}/reversar` | — | `ApiResponse<...>` |
| GET | `/api/movimientos-inventario/kardex` | query | `ApiResponse<List<KardexItemDto>>` |
| POST | `/api/saldos-inventario/recalcular` | `{ bodegaId }` | `ApiResponse<Long>` |
| GET | `/api/saldos-inventario` | query (bodegaId, productoId?) | `ApiResponse<List<SaldoInventarioResponseDto>>` |

## Estados / eventos
- Movimientos sin estado (append-only). Origen rastreado por `origen_modulo`/`origen_id` (compras/facturación lo poblarán por evento).

## Multitenencia y seguridad
- `empresa_id` desde el JWT (TenantContext). Cross-tenant = 404.

## Preguntas abiertas
- ¿Validar stock no-negativo al registrar salida? (v1: no se bloquea; el saldo puede quedar negativo y se detecta en reportes).
- ¿Costo promedio móvil real (ordenado) vs promedio de entradas? (v1: promedio de entradas en el recálculo).
