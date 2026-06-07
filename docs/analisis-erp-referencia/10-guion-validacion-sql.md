# Fase 10 — Guion de validación: consultas SQL de solo lectura

> **Propósito.** Resolver las validaciones ❓ V1–V10 de la [Fase 9 §1.2](09-validacion-diseno-mvp.md)
> y las preguntas bloqueantes (Q1–Q6) **antes** de escribir las migraciones del MVP.
>
> ⚠️ **Reglas de ejecución (obligatorias):**
> 1. Ejecutar **solo sobre una RÉPLICA** de producción, **nunca** sobre la base viva.
> 2. **Solo `SELECT`.** Ninguna sentencia DDL/DML (`INSERT/UPDATE/DELETE/ALTER/CREATE/DROP`).
> 3. Sesión en **solo lectura**: `SET SESSION TRANSACTION READ ONLY;` antes de empezar.
> 4. Motor objetivo: **MySQL** (el del ERP de referencia, [01 §1.1](01-inventario-tecnico.md)).
>    Si la réplica fuera PostgreSQL, ver notas de portabilidad al final (§11).
> 5. Las consultas pesadas (conteos sobre tablas grandes) conviene correrlas en horas de baja carga
>    y con `LIMIT` donde se indique.
>
> **Cómo usar este documento.** Cada bloque corresponde a una validación V#. Ejecuta, **anota el
> resultado** en la columna "Resultado" de tu bitácora y marca la pregunta bloqueante que cierra.
> Reemplaza los marcadores `@org`, `@vigencia`, etc. por valores reales cuando se indique.

```sql
-- Ejecutar una vez al inicio de la sesión
SET SESSION TRANSACTION READ ONLY;
SET SESSION MAX_EXECUTION_TIME = 60000;  -- corta consultas > 60 s (MySQL 5.7+)
```

---

## 0. Reconocimiento inicial del esquema (antes de todo)

Confirma nombres reales de tablas/columnas (pueden variar respecto a lo inferido).

```sql
-- 0.1 ¿Qué base/esquema estoy mirando y cuántas tablas tiene?
SELECT DATABASE() AS esquema_actual;

SELECT COUNT(*) AS total_tablas
FROM information_schema.tables
WHERE table_schema = DATABASE() AND table_type = 'BASE TABLE';

-- 0.2 Tamaño (filas aprox. + MB) de las tablas más grandes — prioriza dónde validar
SELECT table_name,
       table_rows                                   AS filas_aprox,
       ROUND((data_length + index_length)/1024/1024, 1) AS mb
FROM information_schema.tables
WHERE table_schema = DATABASE() AND table_type = 'BASE TABLE'
ORDER BY (data_length + index_length) DESC
LIMIT 40;

-- 0.3 Columnas reales de la tabla-Dios de documentos (confirmar nombres '*_id', 'estado', etc.)
SELECT column_name, data_type, column_type, is_nullable, column_default
FROM information_schema.columns
WHERE table_schema = DATABASE() AND table_name = 'com_encabezados_documentos'
ORDER BY ordinal_position;

-- 0.4 ¿Qué FOREIGN KEYS existen realmente hoy? (mide C2 sobre datos, no sobre migraciones)
SELECT COUNT(*) AS total_fks
FROM information_schema.referential_constraints
WHERE constraint_schema = DATABASE();

SELECT table_name, COUNT(*) AS fks_en_tabla
FROM information_schema.key_column_usage
WHERE table_schema = DATABASE() AND referenced_table_name IS NOT NULL
GROUP BY table_name
ORDER BY fks_en_tabla DESC
LIMIT 30;
```

---

## V1 — ¿Hay datos huérfanos por falta de FK histórica? → cierra parte de Q6

> Patrón: contar filas hijas cuyo `*_id` **no existe** en la maestra. Ajusta nombres tras §0.3.

```sql
-- V1.1 Documentos cuyo tercero principal no existe en el maestro de terceros
SELECT COUNT(*) AS docs_tercero_huerfano
FROM com_encabezados_documentos d
LEFT JOIN com_terceros t ON t.id = d.com_tercero1_id
WHERE d.com_tercero1_id IS NOT NULL
  AND t.id IS NULL;

-- V1.2 Documentos cuya sede no existe
SELECT COUNT(*) AS docs_sede_huerfana
FROM com_encabezados_documentos d
LEFT JOIN com_sedes s ON s.id = d.com_sede1_id
WHERE d.com_sede1_id IS NOT NULL
  AND s.id IS NULL;

-- V1.3 Líneas de detalle sin encabezado (huérfanas del documento padre)
--      Repetir por cada tabla *_detalles_* relevante del MVP (inventario, facturación...)
SELECT COUNT(*) AS detalles_sin_encabezado
FROM inv_detalles_inventarios x
LEFT JOIN com_encabezados_documentos d ON d.id = x.com_encabezado_documento_id
WHERE d.id IS NULL;

-- V1.4 Productos referenciados en facturación que ya no existen en el catálogo
SELECT COUNT(*) AS items_producto_huerfano
FROM fac_detalles_facturacion f
LEFT JOIN com_productos p ON p.id = f.com_producto_id
WHERE f.com_producto_id IS NOT NULL
  AND p.id IS NULL;

-- V1.5 Plantilla genérica reutilizable: % de orfandad de cualquier columna *_id
--      (sustituye TABLA_HIJA / COL_FK / TABLA_MAESTRA)
-- SELECT
--   COUNT(*)                                            AS total,
--   SUM(CASE WHEN m.id IS NULL AND h.COL_FK IS NOT NULL THEN 1 ELSE 0 END) AS huerfanos
-- FROM TABLA_HIJA h
-- LEFT JOIN TABLA_MAESTRA m ON m.id = h.COL_FK;
```

> 🧠 Si los huérfanos son significativos, define reglas de saneamiento **antes** de migrar (mapear a
> un tercero/producto "genérico", o excluir). Esto alimenta la estrategia de migración (Q6).

---

## V2 — ¿Los saldos materializados se descuadran vs. los movimientos? → confirma AD-4/AD-6

> Compara el saldo almacenado contra la suma de movimientos por eje. Diferencia ≠ 0 = descuadre.

```sql
-- V2.1 Contabilidad: saldo por cuenta vs. suma de movimientos contables
--      (ajusta nombres: con_saldos_*, con_detalles_contables, columnas debito/credito)
SELECT s.con_cuenta_id,
       s.saldo                                   AS saldo_materializado,
       COALESCE(SUM(m.debito - m.credito), 0)    AS saldo_calculado,
       s.saldo - COALESCE(SUM(m.debito - m.credito), 0) AS diferencia
FROM con_saldos_centros_costos s
LEFT JOIN con_detalles_contables m
       ON m.con_cuenta_id = s.con_cuenta_id
      AND m.com_vigencia_id = s.com_vigencia_id
GROUP BY s.con_cuenta_id, s.saldo, s.com_vigencia_id
HAVING ABS(diferencia) > 0.01
ORDER BY ABS(diferencia) DESC
LIMIT 50;

-- V2.2 Inventario: saldo de existencias vs. entradas - salidas por producto/bodega
SELECT sp.com_producto_id,
       sp.inv_bodega_id,
       sp.saldo                                  AS saldo_materializado,
       COALESCE(SUM(CASE WHEN di.tipo_movimiento = 1 THEN di.cantidad
                         WHEN di.tipo_movimiento = 2 THEN -di.cantidad
                         ELSE 0 END), 0)          AS saldo_calculado
FROM inv_saldos_productos sp
LEFT JOIN inv_detalles_inventarios di
       ON di.com_producto_id = sp.com_producto_id
      AND di.inv_bodega_id   = sp.inv_bodega_id
GROUP BY sp.com_producto_id, sp.inv_bodega_id, sp.saldo
HAVING ABS(sp.saldo - saldo_calculado) > 0.001
ORDER BY ABS(sp.saldo - saldo_calculado) DESC
LIMIT 50;

-- V2.3 Resumen: ¿cuántas filas de saldo están descuadradas? (decisión migrar vs. recalcular)
SELECT COUNT(*) AS saldos_descuadrados
FROM (
  SELECT sp.id,
         sp.saldo - COALESCE(SUM(CASE WHEN di.tipo_movimiento = 1 THEN di.cantidad
                                      WHEN di.tipo_movimiento = 2 THEN -di.cantidad
                                      ELSE 0 END), 0) AS dif
  FROM inv_saldos_productos sp
  LEFT JOIN inv_detalles_inventarios di
         ON di.com_producto_id = sp.com_producto_id
        AND di.inv_bodega_id   = sp.inv_bodega_id
  GROUP BY sp.id, sp.saldo
) t
WHERE ABS(t.dif) > 0.001;
```

> 🧠 **Decisión que habilita:** si hay descuadres, en el MVP **no se migran los saldos**; se migran
> los movimientos y se **recalcula** la proyección (`saldo_inventario`, `saldo_contable`) — justo lo
> que sostiene AD-4. Si están cuadrados, se confirma que el patrón actual al menos es consistente.

---

## V3 — ¿Qué módulos/tablas están abandonados? → cierra Q13

```sql
-- V3.1 Tablas vacías o sin escritura reciente (recorre todas y reporta MAX fecha)
--      Versión por tabla concreta (repetir para candidatas a no migrar):
SELECT 'aca_cargas_academicas' AS tabla,
       COUNT(*) AS filas,
       MAX(updated_at) AS ultima_actualizacion
FROM aca_cargas_academicas
UNION ALL
SELECT 'jur_encabezados_procesos', COUNT(*), MAX(updated_at) FROM jur_encabezados_procesos
UNION ALL
SELECT 'thu_evaluaciones', COUNT(*), MAX(updated_at) FROM thu_evaluaciones
UNION ALL
SELECT 'acf_activos_fijos', COUNT(*), MAX(updated_at) FROM acf_activos_fijos;

-- V3.2 Generar automáticamente la lista de tablas VACÍAS según el catálogo del motor
--      (table_rows es aproximado en InnoDB; usar como tamiz, confirmar con COUNT(*))
SELECT table_name, table_rows AS filas_aprox
FROM information_schema.tables
WHERE table_schema = DATABASE()
  AND table_type = 'BASE TABLE'
  AND table_rows = 0
ORDER BY table_name;

-- V3.3 Actividad por tipo de documento en los últimos 24 meses (qué se usa de verdad)
SELECT d.com_documento_id,
       COUNT(*)            AS total_docs,
       MAX(d.created_at)   AS ultimo
FROM com_encabezados_documentos d
WHERE d.created_at >= DATE_SUB(CURDATE(), INTERVAL 24 MONTH)
GROUP BY d.com_documento_id
ORDER BY total_docs DESC;
```

---

## V4 — ¿Cuál es el dominio realmente activo? → cierra Q1 (cliente objetivo)

```sql
-- V4.1 ¿Hay datos clínicos/académicos vivos? (si ~0, el MVP comercial es el alcance correcto)
SELECT
  (SELECT COUNT(*) FROM fac_contratos)              AS contratos_salud,
  (SELECT COUNT(*) FROM aca_programas_academicos)   AS programas_academicos,
  (SELECT COUNT(*) FROM nom_vinculaciones)          AS vinculaciones_nomina,
  (SELECT COUNT(*) FROM fac_facturas_electronicas)  AS facturas_electronicas,
  (SELECT COUNT(*) FROM inv_detalles_inventarios)   AS movimientos_inventario;

-- V4.2 Tipos de documento con volumen, con su nombre legible (mapea negocio real)
SELECT td.id, td.nombre, COUNT(d.id) AS docs
FROM com_documentos td
LEFT JOIN com_encabezados_documentos d ON d.com_documento_id = td.id
GROUP BY td.id, td.nombre
ORDER BY docs DESC
LIMIT 40;
```

> 🧠 Si V4.1 muestra que lo vivo es facturación/inventario/cartera y lo clínico/académico está
> inactivo, **Q1 se cierra a favor del MVP comercial** definido en Fase 9.

---

## V5 — Significado real de los `estado` (enteros) → cierra Q5 (máquinas de estado)

```sql
-- V5.1 Distribución de estados por tipo de documento (descubre la máquina de estados real)
SELECT d.com_documento_id,
       d.estado,
       COUNT(*) AS cantidad
FROM com_encabezados_documentos d
GROUP BY d.com_documento_id, d.estado
ORDER BY d.com_documento_id, d.estado;

-- V5.2 Estados en facturación específicamente (para diseñar factura.estado del MVP)
SELECT f.estado, COUNT(*) AS cantidad, MIN(f.created_at) AS desde, MAX(f.created_at) AS hasta
FROM fac_facturas_electronicas f
GROUP BY f.estado
ORDER BY f.estado;

-- V5.3 ¿Existen transiciones observables? (si hay tabla de workflow/historial)
SELECT estado_anterior, estado_nuevo, COUNT(*) AS veces
FROM com_encabezados_documentos_workflow
GROUP BY estado_anterior, estado_nuevo
ORDER BY veces DESC;
```

> 🧠 La lista de `(tipo, estado)` con volumen revela qué estados existen de verdad; las transiciones
> observadas (V5.3) o, en su defecto, una entrevista, definen las flechas válidas del enum (AD-7).

---

## V6 — Reglas de numeración → cierra Q4

```sql
-- V6.1 ¿Cómo está estructurado el consecutivo? (por tipo/sede/vigencia)
SELECT *
FROM com_documentos_consecutivos
ORDER BY com_documento_id
LIMIT 100;

-- V6.2 ¿La numeración reinicia por vigencia? Min/Max de número por (tipo, vigencia, sede)
SELECT d.com_documento_id,
       d.com_vigencia_id,
       d.com_sede1_id,
       MIN(d.numero) AS num_min,
       MAX(d.numero) AS num_max,
       COUNT(*)      AS docs
FROM com_encabezados_documentos d
WHERE d.numero IS NOT NULL
GROUP BY d.com_documento_id, d.com_vigencia_id, d.com_sede1_id
ORDER BY d.com_documento_id, d.com_vigencia_id
LIMIT 100;

-- V6.3 ¿Hay prefijos en el número? Muestra patrones distintos de formato
SELECT DISTINCT
       d.com_documento_id,
       LEFT(d.numero, 4) AS prefijo_posible
FROM com_encabezados_documentos d
WHERE d.numero IS NOT NULL
LIMIT 100;

-- V6.4 Detección de huecos/duplicados en la numeración (integridad del consecutivo)
SELECT d.com_documento_id, d.com_sede1_id, d.com_vigencia_id, d.numero, COUNT(*) AS repetido
FROM com_encabezados_documentos d
WHERE d.numero IS NOT NULL
GROUP BY d.com_documento_id, d.com_sede1_id, d.com_vigencia_id, d.numero
HAVING COUNT(*) > 1
LIMIT 50;
```

---

## V7 — Precisión monetaria real → cierra Q7

```sql
-- V7.1 Declaración de tipo de las columnas de monto (confirma decimal(20,5) u otro)
SELECT table_name, column_name, column_type
FROM information_schema.columns
WHERE table_schema = DATABASE()
  AND data_type = 'decimal'
  AND (column_name LIKE '%valor%' OR column_name LIKE '%total%'
       OR column_name LIKE '%saldo%' OR column_name LIKE '%monto%')
ORDER BY table_name, column_name
LIMIT 100;

-- V7.2 ¿Se usan realmente los decimales? Máximo nº de decimales significativos observado
SELECT MAX(CHAR_LENGTH(SUBSTRING_INDEX(CAST(valor_total AS CHAR), '.', -1))) AS max_decimales_usados
FROM fac_detalles_facturacion
WHERE valor_total IS NOT NULL;
```

> 🧠 Si en la práctica se usan ≤ 2–4 decimales, `NUMERIC(19,4)` basta para el MVP; si hay precios
> unitarios con 5 decimales reales, conservar `(20,5)`. Decide Q7 con este dato, no por defecto.

---

## V8 — Plan de cuentas e impuestos semilla → cierra Q3

```sql
-- V8.1 Plan de cuentas: cuántas cuentas y cuáles son auxiliares (hoja)
SELECT COUNT(*) AS total_cuentas,
       SUM(CASE WHEN auxiliar = 1 THEN 1 ELSE 0 END) AS auxiliares
FROM con_plan_contable;

-- V8.2 Cuentas realmente usadas en movimientos (para sembrar solo lo vivo)
SELECT m.con_cuenta_id, c.codigo, c.nombre, COUNT(*) AS movimientos
FROM con_detalles_contables m
JOIN con_plan_contable c ON c.id = m.con_cuenta_id
GROUP BY m.con_cuenta_id, c.codigo, c.nombre
ORDER BY movimientos DESC
LIMIT 100;

-- V8.3 Catálogo de impuestos/retenciones activos
SELECT *
FROM com_impuestos_deducciones
ORDER BY id
LIMIT 100;
```

---

## V9 — Multitenencia real: nº de organizaciones/sedes → cierra Q2

```sql
-- V9.1 Organizaciones (tenants) registradas y activas
SELECT COUNT(*) AS total_empresas
FROM prv_empresas;

-- V9.2 Empresas con actividad transaccional real (no solo registradas)
SELECT d.empresa_id, COUNT(*) AS docs
FROM com_encabezados_documentos d
GROUP BY d.empresa_id
ORDER BY docs DESC;

-- V9.3 Sedes activas y su volumen (define si multisede pesa en el MVP)
SELECT s.id, s.nombre, COUNT(d.id) AS docs
FROM com_sedes s
LEFT JOIN com_encabezados_documentos d ON d.com_sede1_id = s.id
GROUP BY s.id, s.nombre
ORDER BY docs DESC;
```

> 🧠 Pocas organizaciones grandes con poca actividad cruzada → **BD compartida con `organizacion_id`
> + RLS** (AD-8) es suficiente para v1. Muchos tenants con aislamiento exigido → reconsiderar esquema
> por tenant. Cierra Q2.

---

## V10 — Volumen y antigüedad para dimensionar la migración → cierra Q6

```sql
-- V10.1 Tamaño de las tablas que migraría el MVP (define estrategia: big-bang vs. por fases)
SELECT table_name,
       table_rows AS filas_aprox,
       ROUND((data_length + index_length)/1024/1024, 1) AS mb
FROM information_schema.tables
WHERE table_schema = DATABASE()
  AND table_name IN (
    'com_terceros','com_productos','com_encabezados_documentos',
    'fac_facturas_electronicas','fac_detalles_facturacion',
    'inv_detalles_inventarios','inv_saldos_productos',
    'con_detalles_contables','con_plan_contable',
    'cpc_detalles_cartera','caj_detalles_recibos_caja'
  )
ORDER BY (data_length + index_length) DESC;

-- V10.2 Rango temporal de los datos transaccionales (cuánta historia migrar)
SELECT MIN(created_at) AS dato_mas_antiguo,
       MAX(created_at) AS dato_mas_reciente,
       COUNT(*)        AS total
FROM com_encabezados_documentos;

-- V10.3 Volumen por año (¿migrar solo N años recientes?)
SELECT YEAR(created_at) AS anio, COUNT(*) AS docs
FROM com_encabezados_documentos
GROUP BY YEAR(created_at)
ORDER BY anio;
```

---

## 11. Notas de portabilidad (si la réplica fuese PostgreSQL)

| MySQL | PostgreSQL |
|---|---|
| `SET SESSION TRANSACTION READ ONLY;` | `SET default_transaction_read_only = on;` |
| `MAX_EXECUTION_TIME = 60000` (ms) | `SET statement_timeout = '60s';` |
| `DATABASE()` | `current_schema()` / `current_database()` |
| `information_schema.tables.table_rows` | usar `pg_class.reltuples` (estimado) o `COUNT(*)` |
| `DATE_SUB(CURDATE(), INTERVAL 24 MONTH)` | `CURRENT_DATE - INTERVAL '24 months'` |
| `CHAR_LENGTH`, `SUBSTRING_INDEX` | `length`, `split_part` |
| `LEFT(x, 4)` | `left(x, 4)` (igual) |
| `IFNULL` / `CASE` | `COALESCE` / `CASE` (igual) |

---

## 12. Bitácora de resultados (plantilla para llenar al ejecutar)

| Validación | Consulta | Resultado observado | Decisión que habilita | Pregunta cerrada |
|---|---|---|---|---|
| V1 huérfanos | V1.1–V1.5 | _(filas huérfanas)_ | Reglas de saneamiento | Q6 |
| V2 descuadre saldos | V2.1–V2.3 | _(nº descuadrados)_ | Migrar movimientos y recalcular | AD-4/AD-6 |
| V3 abandono | V3.1–V3.3 | _(tablas vacías)_ | Excluir de migración | Q13 |
| V4 dominio activo | V4.1–V4.2 | _(volumen por dominio)_ | Confirmar MVP comercial | **Q1** |
| V5 estados | V5.1–V5.3 | _(estados por tipo)_ | Diseñar enums | **Q5** |
| V6 numeración | V6.1–V6.4 | _(patrón consecutivos)_ | Servicio de numeración | **Q4** |
| V7 precisión | V7.1–V7.2 | _(decimales usados)_ | `NUMERIC(p,s)` | Q7 |
| V8 plan/impuestos | V8.1–V8.3 | _(cuentas e impuestos)_ | Datos semilla | **Q3** |
| V9 tenants | V9.1–V9.3 | _(nº orgs/sedes)_ | Estrategia multitenencia | **Q2** |
| V10 volumen | V10.1–V10.3 | _(MB y antigüedad)_ | Estrategia de migración | Q6 |

---

### Rastro del trabajo
Guion elaborado el 2026-06-05. **Consultas exclusivamente `SELECT`**, pensadas para ejecutarse sobre
una **réplica de solo lectura**. Los nombres de tablas/columnas provienen del análisis estático
(Fases 01–02) y **deben confirmarse con el bloque §0** antes de ejecutar el resto, pues pueden
diferir de lo inferido. Una vez llena la bitácora §12 y cerradas Q1–Q6, procede la Fase 11
(migraciones Flyway del núcleo).
