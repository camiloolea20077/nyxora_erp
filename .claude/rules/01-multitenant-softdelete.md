# Regla 01 — Multitenencia y soft-delete

## Multitenencia
- Toda tabla de negocio lleva `empresa_id BIGINT NOT NULL` (FK a `empresa`). Excepciones: `empresa`
  (raíz) y catálogos globales (`permiso`, `unidad_medida`).
- `empresa_id`, `usuario_id`, `sede_id` se obtienen del **JWT vía `TenantContext`** (Reactor Context).
  **PROHIBIDO** aceptarlos en DTOs de request.
- Toda consulta del QueryRepository filtra por `empresa_id = :empresaId`.
- **Cross-tenant = 404** con el MISMO mensaje "no encontrado" (nunca 403, nunca 200 con datos ajenos).
  No revelar la existencia de registros de otra empresa.
- `super_admin` solo cruza empresas donde el dominio lo permita (p. ej. gestión de `empresa`).

## Soft-delete
- Eliminar = `deleted_at = now()` + `usuario_modificacion`. **Nunca** `DELETE` físico.
- Toda lectura normal filtra `deleted_at IS NULL`.
- `activo` (Boolean) es bandera de negocio independiente del soft-delete.
- Append-only (sin soft-delete): `auditoria` y futuros `movimiento_contable`/`movimiento_inventario`
  (los errores se corrigen con reversa, no editando/borrando).
