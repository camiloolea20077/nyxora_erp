# Análisis del ERP existente (SinergiaERP) — Documento de referencia para el nuevo ERP

> **Propósito:** Comprender la arquitectura, módulos, procesos y reglas de negocio del ERP
> actual **únicamente como referencia**. Este documento **no propone copiar** el diseño existente;
> interpreta el *concepto de negocio* detrás de cada elemento y propone una arquitectura **propia,
> modular y mejorada** para el nuevo ERP.
>
> **Nota metodológica:** En todo el análisis se distingue explícitamente entre
> 🔎 **Observado** (lo que está en la base de datos / código),
> 🧠 **Interpretación funcional** (qué significa en términos de negocio) y
> 🟢 **Propuesta** (recomendación para el ERP nuevo).
>
> **Sin modificaciones:** No se ejecutó ninguna sentencia DDL/DML sobre la base analizada.
> El estudio se hizo sobre el código fuente (migraciones, modelos y controladores).

---

## Índice de documentos

| # | Documento | Contenido |
|---|-----------|-----------|
| 00 | Este archivo | Resumen ejecutivo e índice |
| 01 | [`01-inventario-tecnico.md`](01-inventario-tecnico.md) | Fase 1 — Motor, esquemas, tablas, FKs, auditoría, configuración |
| 02 | [`02-clasificacion-modulos.md`](02-clasificacion-modulos.md) | Fase 2 — Clasificación funcional de tablas por módulo |
| 03 | [`03-procesos-negocio.md`](03-procesos-negocio.md) | Fase 3 — Reconstrucción de procesos de negocio |
| 04 | [`04-dependencias.md`](04-dependencias.md) | Fase 4 — Mapa de dependencias y acoplamiento |
| 05 | [`05-evaluacion-calidad.md`](05-evaluacion-calidad.md) | Fase 5 — Evaluación de calidad y riesgos |
| 06 | [`06-propuesta-nuevo-erp.md`](06-propuesta-nuevo-erp.md) | Fase 6 — Arquitectura propuesta + ER en Mermaid |
| 07 | [`07-matriz-trazabilidad.md`](07-matriz-trazabilidad.md) | Fase 7 — Matriz de trazabilidad |
| 08 | [`08-backlog-roadmap.md`](08-backlog-roadmap.md) | Fase 8 — Backlog, orden de implementación y preguntas pendientes |
| 09 | [`09-validacion-diseno-mvp.md`](09-validacion-diseno-mvp.md) | Fase 9 — Validación de hallazgos y diseño del MVP (monolito modular Spring Boot + Angular + PostgreSQL) |
| 10 | [`10-guion-validacion-sql.md`](10-guion-validacion-sql.md) | Fase 10 — Guion de validación: consultas SQL de solo lectura (V1–V10) sobre réplica |
| 11 | [`11-migraciones-nucleo.md`](11-migraciones-nucleo.md) | Fase 11 — Migraciones Flyway del núcleo (Administración + Común) en `backend/` |
| 12 | [`12-vertical-slice-tercero.md`](12-vertical-slice-tercero.md) | Fase 12 — Vertical slice de referencia (`tercero`) con el estándar backend v3 (compila) |

---

## 1. Resumen ejecutivo

### ¿Qué es el sistema analizado?

🔎 **Observado / 🧠 Interpretación.** Es un **ERP empresarial del sector salud y educación superior**,
construido sobre **Laravel 5.7 + AsgardCMS** con arquitectura modular (`nwidart/laravel-modules`).
La evidencia (capitación, autorizaciones, copagos, dosis, diagnósticos, grupos quirúrgicos, tipos
de actividad de salud) indica orientación a **IPS/clínicas**, combinada con un módulo **Académico**
(facultades, programas, SNIES, cargas académicas, catedráticos) propio de **universidades**, sobre
una base **ERP transversal** (contabilidad, presupuesto, nómina, tesorería, cartera, facturación).

### Cifras clave del inventario

| Métrica | Valor | Lectura |
|---|---|---|
| Motor por defecto | **MySQL** (soporta pgsql, sqlsrv; MongoDB secundario vía jenssegers) | Multi-driver |
| Migraciones totales | **1.647** | Esquema muy grande y de larga evolución |
| Tablas distintas (`Schema::create`) | **~389** | Modelo extenso |
| Modelos Eloquent (Entities) | **369** | |
| Controladores | **492** | |
| Módulos funcionales | **34** (≈20 con tablas propias) | Alta modularización nominal |
| Migraciones que declaran FK | **329 (≈20%)** → ~992 FKs | **Integridad referencial débil** |
| De esas, en módulo `Custom` | **194** (2024–2025) | FKs **retroajustadas tarde** |
| Migraciones con `softDeletes` | **1.572** | Borrado lógico generalizado ✅ |
| Referencias a `empresa_id` | **1.222** | **Multiempresa** transversal ✅ |
| Referencias a `sede_id` | **750** | **Multisede** transversal ✅ |
| Modelos auditados (owen-it) | **150** | Auditoría parcial |

### Hallazgos más importantes

1. **Tabla-Dios de documentos.** `com_encabezados_documentos` (~50 columnas) almacena *todo*
   documento transaccional (compra, factura, egreso, requisición, etc.) en una sola tabla,
   mezclando campos de presupuesto, inventario, contratos, tesorería y salud, con nombres
   genéricos (`fecha1`, `fecha2`, `com_tercero1_id`, `com_sede2_id`, `valor_capitado`).
   → Over-generalización; ver [05](05-evaluacion-calidad.md).

2. **Integridad referencial mayormente ausente** en el diseño original. Las relaciones se
   resuelven por convención (`*_id`) sin `FOREIGN KEY`. Las FKs reales se agregaron de forma
   tardía y concentrada en el módulo `Custom`.

3. **Módulo `Custom` como "parche".** Redefine y altera tablas de otros módulos
   (`com_subcentros`, `fac_tarifas`, `com_sedes_users`, `audits`), rompiendo la separación modular.

4. **Tablas creadas fuera de su módulo.** P. ej. `nom_detalles_liquidacion_temp` y
   `nom_detalles_liquidacion_temp` se crean dentro de **Académico**; `ctr_contratos` se crea
   tanto en **Inventario** como en **Contratación**. → Fronteras de módulo difusas.

5. **Catálogos genéricos tipo EAV** (`prv_listas_tipos` / `prv_listas_elementos`,
   `prv_maestros_conceptos`, formularios dinámicos) que centralizan parametrización pero
   dificultan la integridad y las consultas.

6. **Fortalezas a conservar:** multiempresa y multisede transversales, borrado lógico
   generalizado, manejo explícito de **vigencias** (`com_vigencias`, `com_apertura_vigencia`),
   y un patrón de **interfaces contables/presupuestales** (`*_detalles_contabilidad`,
   `*_detalles_presupuesto`) replicado en cada módulo transaccional.

### Recomendación de alto nivel

🟢 Diseñar el nuevo ERP sobre un **núcleo de dominio explícito** (no una tabla-Dios), con:
- un **motor de documentos tipado** (un encabezado base + extensiones por tipo de documento),
- **integridad referencial desde el día uno**,
- **fronteras de módulo estrictas** comunicadas por **eventos de dominio** (no por tablas compartidas),
- un **plan de cuentas / presupuesto / terceros** como *capacidades comunes* versionadas por vigencia,
- y **multitenencia (empresa) + multisede** como atributos de primer nivel del núcleo.

Ver la propuesta completa en [`06-propuesta-nuevo-erp.md`](06-propuesta-nuevo-erp.md).

---

## Rastro del trabajo realizado

- Análisis **estático** del repositorio (rama `develop`), sin tocar la BD.
- Extracción programática de: tablas (`Schema::create`), modelos, controladores, FKs,
  `softDeletes`, `empresa_id`, `sede_id`, traits de auditoría.
- Lectura dirigida de migraciones clave (`com_encabezados_documentos`) para confirmar patrones.
- Documentación generada en `docs/analisis-erp-referencia/` (este conjunto de archivos).
