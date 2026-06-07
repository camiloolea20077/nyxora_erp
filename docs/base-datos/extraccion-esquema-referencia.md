# Extracción del esquema real del ERP de referencia (300+ tablas)

> **Por qué.** El análisis (fases 00–12) fue estático: tenemos los **nombres** de ~389 tablas y su
> propósito, pero NO sus **columnas reales**. Para diseñar el MVP rico (no pequeño) necesitamos los
> campos exactos. Este documento da scripts **de solo lectura** para extraer el esquema real desde
> el ERP anterior y traerlo como insumo del diseño de Nyxora.
>
> ⚠️ **Solo lectura / solo esquema.** Ejecutar sobre una **réplica o copia**, nunca sobre producción.
> Ningún `INSERT/UPDATE/DELETE/DROP`. Motor de origen: **MySQL** (según fase 01); incluyo variantes.

---

## Opción A — Tienes acceso a la BD (MySQL) → la vía más fiel

### A.1 Volcado completo del esquema (las 300+ tablas, SIN datos)
Esto produce un `.sql` ejecutable con **todas** las tablas y sus columnas/índices/FK reales:

```bash
mysqldump --no-data --skip-comments --routines=false --triggers=false \
  -h HOST -u USUARIO -p NOMBRE_BD > 00_esquema_referencia.sql
```

> `--no-data` = solo estructura. Ese archivo ES "el script de las 300+ tablas" tal cual existen hoy.

### A.2 Diccionario de columnas (legible, para mapear al diseño nuevo)
```sql
-- Todas las columnas de todas las tablas
SELECT table_name, ordinal_position, column_name, column_type,
       is_nullable, column_key, column_default, extra, column_comment
FROM information_schema.columns
WHERE table_schema = 'NOMBRE_BD'
ORDER BY table_name, ordinal_position;
```
Exportar a CSV (ej. desde el cliente, o):
```bash
mysql -h HOST -u USUARIO -p -e "SELECT table_name,ordinal_position,column_name,column_type,is_nullable,column_key,column_default \
  FROM information_schema.columns WHERE table_schema='NOMBRE_BD' ORDER BY table_name,ordinal_position;" \
  NOMBRE_BD > 01_diccionario_columnas.tsv
```

### A.3 Foreign keys reales
```sql
SELECT table_name, column_name, constraint_name,
       referenced_table_name, referenced_column_name
FROM information_schema.key_column_usage
WHERE table_schema = 'NOMBRE_BD' AND referenced_table_name IS NOT NULL
ORDER BY table_name, column_name;
```

### A.4 Qué tablas se usan de verdad (conteo de filas) — para no migrar tablas muertas
```sql
SELECT table_name, table_rows
FROM information_schema.tables
WHERE table_schema = 'NOMBRE_BD' AND table_type = 'BASE TABLE'
ORDER BY table_rows DESC;
```

### A.5 Foco en una tabla concreta (ej. el `tercero` real con sus 20+ campos)
```sql
SHOW CREATE TABLE com_terceros;
-- y sus satélites:
SHOW CREATE TABLE com_terceros_contactos;
SHOW CREATE TABLE com_terceros_correos;
SHOW CREATE TABLE com_terceros_tipos_actividades;
```

---

## Opción B — La BD es PostgreSQL (réplica)
```bash
pg_dump --schema-only --no-owner --no-privileges \
  -h HOST -U USUARIO -d NOMBRE_BD > 00_esquema_referencia.sql
```
Diccionario equivalente: usar `information_schema.columns` (igual que A.2) con `table_schema='public'`.

---

## Opción C — Solo tienes el repositorio Laravel (sin BD)
Las migraciones SON el esquema. Comparte la ruta del repo (carpeta `database/migrations` y
`Modules/*/Database/Migrations`) y yo extraigo, por cada `Schema::create`, la lista de columnas y
FKs directamente del código. Es más lento que un dump, pero no requiere BD.

---

## Qué hacer con la salida
Entrégame (o coloca en `docs/base-datos/`):
- `00_esquema_referencia.sql` (estructura completa), y/o
- `01_diccionario_columnas.tsv` (columnas), `02_fks.tsv`, `03_conteos.tsv`.

Con eso construyo, **por cada módulo del MVP**, las migraciones Nyxora **ricas** (todos los campos
de negocio reales mapeados a un diseño limpio: con FK, sin tabla-Dios, sin EAV), y actualizo
`.claude/data/diccionario-datos.md`. **No** recrearé las 300+ tablas verbatim (eso reimporta el
bloat); sí garantizaré que ninguna columna de negocio relevante se pierda.

## Mapeo: 389 tablas de referencia → MVP Nyxora
- **Entran al MVP (8 módulos):** Administración, Común, Compras, Inventario, Facturación, Caja,
  Cartera, Contabilidad básica → ver `../analisis-erp-referencia/09`.
- **Se posponen:** Nómina, Presupuesto, Tesorería, CxP, Activos fijos, Académico, Salud, etc.
- **Se descartan:** módulo `Custom` (parches), tablas vacías/abandonadas (las revela A.4), EAV genérico.
