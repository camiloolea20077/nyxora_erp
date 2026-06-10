# HU-0003 — Productos, categorías e impuestos

| Campo | Valor |
|---|---|
| **Código** | HU-0003 |
| **Módulo** | Común |
| **Estado** | En desarrollo |
| **Prioridad** | Alta |
| **Dependencias** | HU-0001 (auth); HU-0002 (tercero=proveedor); migración V2 (impuesto), V6 (categoria, producto, satélites), V14 (recurso) |

## Historia
> **Como** usuario de Común
> **quiero** administrar el catálogo de productos (con su categoría, variantes, proveedores e impuestos)
> **para** disponer de un maestro comercial/logístico único por empresa que alimente inventario, compras y facturación.

## Criterios de aceptación (Gherkin)
```gherkin
Escenario: Crear impuesto
  Dado un impuesto con código no registrado para la vigencia en mi empresa
  Cuando hago POST /api/impuestos
  Entonces se crea con activo=true y empresa_id de mi token, y recibo 201

Escenario: Crear categoría jerárquica
  Dado una categoría padre existente de mi empresa
  Cuando hago POST /api/categorias con categoriaPadreId
  Entonces se crea bajo ese padre y recibo 201

Escenario: Crear producto con clasificación e impuestos
  Dado una categoría e impuesto por defecto de mi empresa
  Cuando hago POST /api/productos con impuestoId y productoImpuestoIds
  Entonces se crea el producto y se enlazan sus impuestos adicionales

Escenario: Código de producto duplicado
  Dado un producto ya existente con ese código en mi empresa
  Cuando hago POST /api/productos
  Entonces recibo 400 "Ya existe un producto con ese código"

Escenario: Acceso cross-tenant
  Dado un producto de otra empresa
  Cuando hago GET /api/productos/{id}
  Entonces recibo 404 "Producto no encontrado"
```

## Reglas de negocio
- RN1: `codigo` único por empresa en `producto`, `categoria`; `impuesto` único por (empresa, código, vigencia).
- RN2: `producto.tipo ∈ {bien, servicio}`; `impuesto.tipo ∈ {iva, retencion, ica, otro}`; `tarifa ≥ 0`.
- RN3: La categoría es jerárquica (autoreferencia `categoria_padre_id`), de la misma empresa.
- RN4: Satélites de producto (variante/proveedor/impuesto) se gestionan bajo un producto de la empresa; producto inexistente/ajeno = 404.
- RN5: `proveedor_id` de `producto_proveedor` debe ser un `tercero` vigente de la empresa.
- RN6: Eliminar = soft-delete (`deleted_at`); `empresa_id`/`usuario_id` del JWT, nunca del body.

## Datos (consultar agente base-datos)
- **Entidades dueñas:** `impuesto` (V2), `categoria`, `producto`, `producto_variante`, `producto_proveedor`, `producto_impuesto` (V6).
- **Migración nueva requerida:** no (existen en V2/V6).

## Contrato de API
| Método | Ruta | Request | Response |
|---|---|---|---|
| POST/PUT/DELETE/GET | `/api/impuestos` | `Create/UpdateImpuestoRequestDto` | `ApiResponse<ImpuestoResponseDto>` |
| POST | `/api/impuestos/list` | `PageableDto` | `ApiResponse<PageResponse<ImpuestoTableDto>>` |
| POST/PUT/DELETE/GET | `/api/categorias` | `Create/UpdateCategoriaRequestDto` | `ApiResponse<CategoriaResponseDto>` |
| POST/PUT/DELETE/GET | `/api/productos` | `Create/UpdateProductoRequestDto` | `ApiResponse<ProductoResponseDto>` |
| POST | `/api/productos/list` | `PageableDto` | `ApiResponse<PageResponse<ProductoTableDto>>` |
| GET/POST/PUT/DELETE | `/api/productos/{id}/variantes` etc. | satélites | `ApiResponse<…>` |

## Multitenencia y seguridad
- `empresa_id` desde el JWT (TenantContext). Cross-tenant = 404 con el mismo mensaje "no encontrado".

## Preguntas abiertas
- ¿Las variantes/proveedores se envían embebidos al crear el producto o como endpoints satélite? (se opta por satélite, como en Terceros).
- ¿`impuesto_vigencia` (tarifa histórica) se modela ahora o se difiere? (se difiere; tarifa vigente en `impuesto`).
