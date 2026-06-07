# Fase 11 — Migraciones del núcleo (Administración + Común)

> **Propósito.** Materializar el núcleo del MVP (módulos **Administración** y **Común** de la
> [Fase 9](09-validacion-diseno-mvp.md)) como **migraciones Flyway reales** sobre PostgreSQL,
> aplicando las decisiones arquitectónicas AD-1 … AD-11.
>
> **Convención de equipo (backend v3).** Estas migraciones siguen el estándar del equipo, que
> **prevalece** sobre dos decisiones de la Fase 9: (a) la multitenencia usa la columna **`empresa_id`**
> (no `organizacion_id`) y la tabla raíz se llama **`empresa`**; (b) las tablas viven en un **esquema
> `public` plano** (no un esquema por módulo). La auditoría usa `created_at`/`updated_at`/`deleted_at`
> (inglés) + `usuario_creacion`/`usuario_modificacion` (español) + `activo` Boolean. Ver
> reconciliación en [Fase 9 §6](09-validacion-diseno-mvp.md) (nota AD-2/AD-8).
>
> **Alcance.** Solo el **núcleo**. Los módulos transaccionales (Compras, Inventario, Facturación,
> Caja, Cartera, Contabilidad) se escriben en migraciones `V4+` **después** de cerrar las preguntas
> bloqueantes Q1–Q6 con la [Fase 10](10-guion-validacion-sql.md).
>
> ⚠️ **Por qué el núcleo sí puede escribirse ya:** Administración y Común no dependen de los
> hallazgos a validar sobre la BD viva. La numeración, los terceros, los productos y las vigencias
> son estructuras del **diseño nuevo**, no migraciones de datos del sistema de referencia. Lo que
> **sí espera** validación es el *contenido semilla* (plan de cuentas, impuestos, tipos de
> documento), no el *esquema*.

---

## 1. Artefactos generados

```
backend/
└── src/main/resources/
    ├── application.yml                         # datasource + Flyway + JPA validate
    └── db/migration/
        ├── V1__administracion_schema.sql       # módulo Administración (10 tablas, public)
        ├── V2__comun_schema.sql                # módulo Común (11 tablas, public)
        └── V3__seed_nucleo.sql                 # semilla SEGURA (permisos + unidades)
```

| Archivo | Contenido | Tablas |
|---|---|---|
| `V1__administracion_schema.sql` | Plataforma: tenant, sedes, RBAC, vigencias, parámetros, auditoría | `empresa`, `sede`, `usuario`, `rol`, `permiso`, `rol_permiso`, `usuario_rol`, `vigencia`, `parametro`, `auditoria` |
| `V2__comun_schema.sql` | Maestros + motor mínimo de documentos | `tercero`, `tercero_rol`, `unidad_medida`, `producto`, `impuesto`, `producto_impuesto`, `centro_costo`, `lista`, `lista_item`, `tipo_documento`, `consecutivo` |
| `V3__seed_nucleo.sql` | Semilla **solo** independiente de validación | Permisos base (18) + unidades de medida (11) |

> Todas las tablas viven en `public`. `empresa` es la raíz de la multitenencia (no lleva `empresa_id`);
> `permiso` y `unidad_medida` son catálogos **globales** de referencia (tampoco llevan `empresa_id`).

---

## 2. Convenciones aplicadas (y su trazabilidad al análisis)

| Convención | Implementación | Corrige / aplica |
|---|---|---|
| **Esquema `public` plano** | tablas sin prefijo de esquema (`empresa`, `tercero`…) | convención backend v3 (SQL nativo sin prefijo en QueryRepository) |
| **FK desde el día 1** | `FOREIGN KEY … REFERENCES` en cada relación | AD-5 · corrige C2 (~20% de FK en el sistema actual) |
| **Dependencias acíclicas** | módulos transaccionales → núcleo (`empresa`, `vigencia`, `sede`) | AD-2 (frontera lógica, no por esquema) |
| **Unicidad real** | `UNIQUE (empresa_id, codigo)` por tenant | evita duplicados que el sistema actual permitía |
| **Estados como texto + CHECK** | `CHECK (estado IN (...))` | AD-7 · reemplaza `tinyInteger estado` mágico (C8) |
| **PK identidad** | `BIGINT GENERATED ALWAYS AS IDENTITY` | estándar SQL, sin `increments` propietario |
| **Auditoría de columnas** | `created_at`, `updated_at`, `deleted_at`, `usuario_creacion`, `usuario_modificacion`, `activo` | trazabilidad uniforme (AD-9) + estándar v3 |
| **Borrado lógico selectivo** | `deleted_at` en maestros; **ausente** en `auditoria` y asociaciones | AD-6 · soft-delete donde aplica, append-only donde no |
| **Documentación en BD** | `COMMENT ON TABLE/COLUMN` poblados | corrige el `comment('')` vacío masivo (C5/[05](05-evaluacion-calidad.md)) |
| **Multitenencia por columna** | `empresa_id` + índice en todas las tablas de negocio | AD-8 + estándar v3 |
| **Numeración atómica** | tabla `consecutivo` con `UNIQUE (tipo, sede, vigencia)` | AD-10 · el incremento usará `SELECT … FOR UPDATE` en la app |
| **Catálogos tipados** | `lista`/`lista_item` con FK | reemplaza el EAV `prv_listas_*` |

> 🧠 **Decisión: `VARCHAR + CHECK` en vez de `ENUM` nativo de PostgreSQL.** Los enums nativos exigen
> `ALTER TYPE` para evolucionar y complican migraciones/rollback. `VARCHAR(n) + CHECK` da la misma
> garantía de dominio con migraciones triviales. Las transiciones de estado se validan en la capa de
> aplicación (AD-7), no en la BD.
>
> 🧠 **Frontera de módulo sin esquema propio.** Al optar por `public` plano (convención v3), la
> frontera entre módulos deja de verificarla el motor y pasa a garantizarse en la **capa de
> aplicación**: paquetes por bounded context y el QueryRepository de cada módulo como único punto de
> acceso a sus tablas. AD-2 se mantiene como regla de código, no de esquema.

---

## 3. Qué NO se sembró todavía (y por qué)

| No sembrado | Depende de | Irá en |
|---|---|---|
| Plan de cuentas | Q3 / V8 — cuentas reales y cuáles son auxiliares | `V_contabilidad_*` |
| Impuestos (IVA/retefuente) con tarifa | Q3 / V8 — tarifas vigentes reales | semilla del módulo Común ampliada |
| Tipos de documento (códigos, prefijos, reinicio) | Q4 / V6 — reglas de numeración reales | semilla tras validación |
| Organización y sedes concretas | Alta operativa (no es semilla técnica) | datos de puesta en marcha |

> Sembrar estos catálogos **antes** de validar reproduciría el error de inventar estructura sobre
> supuestos. La semilla `V3` se limitó a lo que es cierto independientemente del cliente: permisos
> del RBAC y unidades de medida estándar.

---

## 4. Cómo ejecutar (local)

```bash
# 1. Levantar PostgreSQL (ejemplo con Docker)
docker run --name erp-pg -e POSTGRES_DB=erp_mvp -e POSTGRES_USER=erp \
  -e POSTGRES_PASSWORD=erp -p 5432:5432 -d postgres:16

# 2. Las migraciones corren solas al arrancar Spring Boot (spring.flyway.enabled=true)
./mvnw spring-boot:run

# — o aplicarlas con el CLI de Flyway sin levantar la app —
flyway -url=jdbc:postgresql://localhost:5432/erp_mvp -user=erp -password=erp \
  -locations=filesystem:backend/src/main/resources/db/migration migrate
```

`spring.jpa.hibernate.ddl-auto: validate` garantiza que **Flyway es la única fuente de verdad del
esquema**; Hibernate solo valida que las entidades coincidan, nunca genera DDL.

---

## 5. Verificación post-migración (consultas de comprobación)

```sql
-- 21 tablas de negocio creadas en public (10 de Administración + 11 de Común)
-- (excluye la tabla de control flyway_schema_history)
SELECT COUNT(*) AS tablas
FROM information_schema.tables
WHERE table_schema = 'public'
  AND table_type = 'BASE TABLE'
  AND table_name <> 'flyway_schema_history';

-- Todas las FK quedaron declaradas (a diferencia del sistema de referencia)
SELECT COUNT(*) AS total_fks
FROM information_schema.referential_constraints
WHERE constraint_schema = 'public';

-- Semilla aplicada
SELECT (SELECT COUNT(*) FROM permiso)       AS permisos,   -- 18
       (SELECT COUNT(*) FROM unidad_medida) AS unidades;   -- 11
```

---

## 6. Próximos pasos

1. **Ejecutar la [Fase 10](10-guion-validacion-sql.md)** sobre la réplica y llenar su bitácora §12.
2. Con Q1–Q6 cerradas, escribir `V4+`:
   - `V4__seed_tipos_documento.sql` (tras Q4/V6).
   - `V5__contabilidad_schema.sql` + plan de cuentas semilla (tras Q3/V8).
   - `V6__compras_schema.sql`, `V7__inventario_schema.sql`, `V8__facturacion_schema.sql`,
     `V9__cartera_schema.sql`, `V10__caja_schema.sql`.
3. **Esqueleto Spring Boot modular** (paquetes por bounded context + eventos de dominio in-process):
   pendiente como Fase 12 si se desea.

---

### Rastro del trabajo
Migraciones y configuración generadas el 2026-06-05 para PostgreSQL, aplicando las decisiones de la
Fase 9. **No se ejecutó ninguna operación sobre la base de datos del ERP de referencia.** El esquema
del núcleo es independiente de las validaciones pendientes; el contenido semilla sensible a validación
quedó explícitamente diferido (§3).
