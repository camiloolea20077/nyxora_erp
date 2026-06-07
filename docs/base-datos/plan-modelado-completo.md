# Plan de modelado COMPLETO de la base de datos (no solo MVP)

> Objetivo: modelar **todos** los módulos del ERP de referencia (~390 tablas en `public` + `his`)
> al diseño limpio de Nyxora. **Primero la BD completa, luego los sprints.**
> Criterio constante: mapear el real → nombres limpios, FK reales, sin tabla-Dios/EAV, smallint 0/1→boolean,
> json-listas→satélites, movimientos append-only, saldos como proyección.

## Estado por módulo

| # | Módulo | Origen | Migración | Estado |
|---|---|---|---|---|
| 1 | Administración | (núcleo) | V1 | ✅ |
| 2 | Común (catálogos, terceros, productos, organización) | com_, prv_ | V2,V4–V7 | ✅ |
| 3 | Contabilidad | con_ | V8 | ✅ |
| 4 | Inventario | inv_ | V9 | ✅ |
| 5 | Compras | cmp_/ctr_ | V10 | ✅ |
| 6 | Facturación | fac_ | V11 | ✅ |
| 7 | Cartera | cpc_ | V12 | ✅ |
| 8 | Caja | caj_ | V13 | ✅ |
| 9 | **Costos** | cos_ | V14 | ✅ |
| 10 | **Presupuesto** | pre_ (19) | V15 | ✅ |
| 11 | **Tesorería** | tes_ (15) | V16 | ✅ |
| 12 | **Cuentas por Pagar** | cpp_ (6) | V17 | ✅ |
| 13 | **Activos Fijos** | acf_ (6) | V18 | ✅ |
| 14 | **Contratación** | ctr_ (7) | V19 | ✅ |
| 15 | **Talento Humano** | thu_ (16) | V20 | ✅ |
| 16 | **Nómina** (núcleo) | nom_ (60) | V21 | ✅ (núcleo; variantes finas en 2ª pasada) |
| 17 | **Académico** | aca_ (11) | V22 | ✅ |
| 18 | **Jurídico/Disciplinarios** | jur_ (6) | V23 | ✅ |
| 19 | Vertical Salud (historia clínica) | his.*, cir_ | — | ⛔ EXCLUIDO por decisión del usuario |
| 20 | Soporte transversal (adjuntos) | com_adjuntos | V25 | ✅ (adjunto polimórfico) |
| — | Semillas catálogos globales | — | V24 | ✅ |

> **BD MODELADA COMPLETA** (excepto Salud): 126 tablas en V1–V25. Falta solo: semillas por-empresa
> (plan de cuentas, impuestos, tipos de documento) al crear cada empresa, importar geografía DANE/CIIU
> completos del real, ejecutar Flyway y arrancar los sprints.

## Se DESCARTA (no se modela)
- Módulo `Custom` (parches), `prv_listas_*`/formularios dinámicos (EAV), Sentinel/AsgardCMS
  (`users`/`roles`/`media`/`page`/`tag`/`translation`/`menu`), `jobs`/`failed_jobs`/`migrations`,
  `databasechangelog*`. La seguridad se rehace con JWT propio (módulo Administración).

## Notas de dependencia (orden de migración)
- Costos (recurso) se usa en terceros/productos/contabilidad → temprano.
- Presupuesto usa centro_costo/proyecto (V7), tercero (V5).
- Tesorería usa cuenta contable (V8), banco/forma_pago (V4).
- Nómina depende de Talento Humano y Académico (carga docente).
- Salud (his) es vertical aparte; se mapea desde el diagrama `his` del usuario.

## Después de la BD: sprints
El plan de fases/sprints (`docs/roadmap-fases-sprints.md`) se **re-priorizará** una vez completa la BD,
para construir los vertical slices reactivos por módulo/HU.
