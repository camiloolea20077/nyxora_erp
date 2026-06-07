# Fase 1 — Inventario técnico

> 🔎 **Observado** del código fuente (config + migraciones). No se consultó la BD en vivo.

## 1.1 Motor de base de datos

| Conexión | Driver | Uso inferido |
|---|---|---|
| `default` | **MySQL** (`env('DB_CONNECTION','mysql')`) | Base transaccional principal |
| `pgsql` / `pgsql_*` | PostgreSQL | Conexiones alternativas/secundarias configuradas |
| `sqlsrv` | SQL Server | Compatibilidad / integraciones |
| `sqlite` | SQLite | Tests / local |
| `mongodb` (`jenssegers/mongodb`) | MongoDB | Almacenamiento secundario (`DB_HOST_SECOND`), p. ej. logs/no estructurado |

🧠 **Interpretación.** El código está escrito para ser **multi-motor** vía la abstracción de
Eloquent, pero el diseño efectivo apunta a **MySQL** (tipos `tinyInteger`, `decimal(20,5)`,
`json`). La presencia de MongoDB sugiere un almacén complementario para datos voluminosos o
semiestructurados (auditoría/colas/documentos).

## 1.2 Esquemas

🔎 No hay esquemas SQL nominales separados; la **segmentación lógica** se hace por **prefijo de
tabla** y por **carpeta de módulo** Laravel.

| Prefijo | Módulo | | Prefijo | Módulo |
|---|---|---|---|---|
| `com_` | Común (núcleo transversal) | | `tes_` | Tesorería |
| `prv_` | Privado / parametrización | | `caj_` | Caja |
| `nom_` | Nómina | | `cpc_` | Cartera (cuentas por cobrar) |
| `aca_` | Académico | | `cpp_` | Cuentas por pagar |
| `pre_` | Presupuesto | | `acf_` | Activos fijos |
| `con_` | Contabilidad | | `thu_` | Talento humano |
| `fac_` | Facturación | | `ctr_` | Contratación |
| `inv_` | Inventario | | `cmp_` | Compras |
| `cos_` | Costos | | `jur_` | Procesos disciplinarios (jurídico) |
| `adm_` | Administración | | `cir_` | Quirúrgico (salud) |

🟢 *Propuesta:* en el ERP nuevo usar **esquemas/namespaces reales por módulo** (o bases por
bounded-context) en lugar de prefijos de nombre, para que la frontera sea verificable por el motor.

## 1.3 Tablas

- **~389 tablas distintas** creadas vía `Schema::create`.
- Distribución por módulo (por nº de migraciones, indicador de actividad/tamaño):

```
Comun        352   Activofijo      33
Nomina       311   Talentohumano   32
Custom       216   Cuentaspagar    24
Presupuesto   91   Caja            17
Inventario    91   Cartera         14
Facturacion   89   User            13
Contabilidad  84   Menu/Contrat./Admin ~10
Tesoreria     73   Procesosdisc.    7
Privado       59   Setting/Media    5
Academico     55   Translation      4
Compras       35   Page/Tag/Dashboard ≤3
```

> El módulo **Custom (216 migr.)** no es un módulo de negocio: es un acumulador de parches/ajustes
> sobre tablas de otros módulos. Ver [05](05-evaluacion-calidad.md).

## 1.4 Columnas y tipos de datos

🔎 Patrones recurrentes observados (ej. `com_encabezados_documentos`):
- PK: `increments('id')` (entero autoincremental sin signo).
- Montos: `decimal(20,5)` — alta precisión monetaria.
- Estados: `tinyInteger('estado')` con `default` numérico (máquina de estados por número, sin enum).
- Fechas: `datetime`, con campos genéricos `fecha1`, `fecha2`, `fecha_base1`, `fecha_base2`.
- Relaciones: columnas `*_id` `unsigned` **sin** definición de FK en la mayoría de casos.
- `json` para datos flexibles (`documento_externo`).
- `comment('')` masivamente **vacío** → documentación de columnas casi inexistente.

## 1.5 Llaves primarias

🔎 PK simple entera autoincremental en prácticamente todas las tablas. Las tablas pivote
(`*_pivote`, `*_x_*`) también usan PK propia más que PK compuesta.

## 1.6 Llaves foráneas

| Indicador | Valor |
|---|---|
| Migraciones que declaran `->foreign(` | **329 de 1.647 (~20%)** |
| FKs totales aprox. | **~992** |
| FKs creadas dentro del módulo `Custom` | **194 archivos (~59% de las FK)** |

🧠 **Interpretación crítica.** El esquema original se construyó **sin integridad referencial**;
las relaciones son por convención de nombres. Las FKs reales se **retroajustaron en 2024–2025**
y se concentraron en `Custom`. Esto implica años de operación con riesgo de **datos huérfanos**.

## 1.7 Índices

🔎 Índices explícitos escasos más allá de los implícitos por PK y los creados al añadir FK.
🧠 Con ~389 tablas y montos/fechas como filtros frecuentes, es probable que existan **consultas
sin índice de cobertura** (ver rendimiento en [05](05-evaluacion-calidad.md)).

## 1.8 Restricciones

🔎 Predomina la validación en **capa de aplicación** (Requests/Eloquent), no en BD: pocos
`unique`, `check` o `default` semánticos; los estados son enteros sin restricción de dominio.

## 1.9 Vistas, procedimientos, funciones, triggers, secuencias

🔎 **No** se hallaron `CREATE VIEW/PROCEDURE/FUNCTION/TRIGGER` en SQL crudo dentro de las
migraciones (0 coincidencias). La lógica vive en **PHP** (controladores/servicios). Secuencias =
autoincrement nativo. Los **consecutivos de documento** se gestionan por aplicación
(`com_documentos_consecutivos`), no por secuencia de BD.

🧠 Ventaja: portabilidad multi-motor. Riesgo: lógica de negocio crítica (numeración, saldos)
fuera de transacciones atómicas del motor.

## 1.10 Tablas de auditoría

| Tabla | Origen | Función |
|---|---|---|
| `audits` | `owen-it/laravel-auditing` | Auditoría genérica de cambios (old/new values) — **150 modelos** la usan |
| `audits_login` | Administración/Custom | Auditoría de inicios de sesión |
| `com_log_archivos`, `*_procesos_logs`, `*_errores` | Por módulo | Logs de procesos masivos (nómina, presupuesto, facturación, importador) |

🧠 Auditoría **parcial** (150/369 modelos). Procesos masivos sí registran logs y errores.

## 1.11 Tablas de configuración / parametrización

| Tabla | Función inferida |
|---|---|
| `prv_empresas`, `prv_tipos_empresas` | **Multiempresa** (tenant) |
| `com_sedes`, `com_sedes_modulos` | **Multisede** y habilitación de módulos por sede |
| `com_vigencias`, `com_apertura_vigencia(_detalle)` | **Vigencias** (años fiscales) y su apertura/cierre |
| `prv_parametros`, `prv_parametros_secciones`, `prv_parametros_correos` | Parámetros del sistema |
| `prv_listas_tipos`, `prv_listas_elementos` | **Catálogo genérico EAV** (listas parametrizables) |
| `prv_maestros_conceptos(_xref)`, `prv_maestros_documentos` | Maestros de conceptos/tipos de documento |
| `prv_modulos`, `prv_modulos_objetos`, `prv_objetos` | Registro de módulos/objetos (permisos/menú) |
| `com_formularios*`, `com_*_formularios_dinamicos` | **Formularios dinámicos** (definición de UI/datos en BD) |
| `settings`, `menus`, `roles` (Sentinel) | Configuración base AsgardCMS / seguridad |

🟢 *Propuesta:* conservar el concepto de **vigencias, multiempresa y multisede** como ciudadanos
de primer nivel, pero reemplazar el EAV genérico por **catálogos tipados por dominio** con
integridad referencial (ver [06](06-propuesta-nuevo-erp.md)).
