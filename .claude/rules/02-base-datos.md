# Regla 02 — Base de datos y migraciones

- Motor **PostgreSQL 16**, esquema **`public`** plano (sin prefijo por módulo).
- PK `BIGINT GENERATED ALWAYS AS IDENTITY`. Montos `NUMERIC(19,4)` (confirmar con validación V7).
- Estados como `VARCHAR + CHECK` (no enum nativo, para migrar sin `ALTER TYPE`).
- Integridad referencial desde el día 1: `FOREIGN KEY`, `UNIQUE`, `CHECK`. Índice en `empresa_id`.
- `COMMENT ON TABLE/COLUMN` en lo no obvio (no dejar columnas sin documentar).

## Migraciones Flyway
- Ubicación: `src/main/resources/db/migration/`. Nombre: `V<n>__<modulo>_<proposito>.sql`.
- Una migración aplicada es **inmutable** (nunca editarla; crear una nueva).
- Núcleo existente: `V1__administracion_schema`, `V2__comun_schema`, `V3__seed_nucleo`.
- Semillas sensibles a validación (plan de cuentas, impuestos, tipos de documento) → `V4+`, solo
  tras cerrar las preguntas bloqueantes (`docs/analisis-erp-referencia/10`).
- Antes de escribir SQL para una HU, consultar campos reales con el agente **base-datos**
  (`.claude/data/diccionario-datos.md`). Mantener ese diccionario sincronizado con cada migración.
