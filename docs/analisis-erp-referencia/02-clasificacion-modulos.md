# Fase 2 — Clasificación funcional por módulo

> Para cada módulo: finalidad, tablas representativas, importancia, relaciones y riesgos.
> 🔎 Observado · 🧠 Interpretación. (El detalle tabla a tabla está en la matriz, doc [07](07-matriz-trazabilidad.md).)

Leyenda de importancia: 🔴 crítico · 🟠 alto · 🟡 medio · ⚪ soporte.

---

## Común (`com_`) — Núcleo transversal · 🔴

🧠 No es un "módulo de negocio": es el **núcleo** del que dependen casi todos. Concentra
demasiadas responsabilidades heterogéneas.

| Sub-área | Tablas representativas | Finalidad |
|---|---|---|
| **Maestro de terceros** | `com_terceros`, `com_terceros_contactos`, `com_terceros_correos`, `com_terceros_tipos_actividades`, `com_terceros_huellas` | Clientes/proveedores/empleados/pacientes unificados |
| **Motor de documentos** | `com_encabezados_documentos`, `com_documentos`, `com_documentos_consecutivos`, `com_encabezados_documentos_referencias`, `com_encabezados_documentos_workflow`, `com_actos_administrativos` | **Documento universal** + numeración + referencias + workflow |
| **Productos / catálogo** | `com_productos`, `com_producto_variantes`, `com_producto_variante_atributos`, `com_productos_compuestos`, `com_productos_proveedores`, `com_productos_dosis`, `com_productos_calidad` | Catálogo de bienes/servicios (con dosis → salud) |
| **Org. y costos** | `com_sedes`, `com_dependencias`, `com_centros_costos`, `com_subcentros`, `com_proyectos` | Estructura organizacional y centros de costo |
| **Vigencias / tributario** | `com_vigencias`, `com_apertura_vigencia`, `com_impuestos_deducciones*`, `com_vigencias_margenes_retenciones` | Años fiscales e impuestos/retenciones |
| **Formularios dinámicos** | `com_formularios*`, `com_*_formularios_dinamicos`, `com_preguntas/respuestas*` | Definición de formularios en BD |
| **Documentación/Calidad** | `com_documentos_calidad`, `com_productos_calidad`, `com_adjuntos`, `com_objetos_adjuntos` | Gestión documental y adjuntos |
| **Salud (mezclado aquí)** | `com_tipos_actividades_salud`, `com_supervencion_intervencion`, `com_cgrs` | Conceptos clínicos en el núcleo (mezcla de dominio) |
| **Importación / jobs** | `com_importador*`, `jobs`, `failed_jobs`, `notifications` | Cargas masivas y colas |

⚠️ **Riesgos:** tabla-Dios de documentos; mezcla de dominio (salud dentro del núcleo);
EAV de formularios; 352 migraciones → módulo sobrecargado.

---

## Privado (`prv_`) — Parametrización / multitenencia · 🔴

| Tablas | Finalidad |
|---|---|
| `prv_empresas`, `prv_tipos_empresas` | **Tenant / multiempresa** |
| `prv_parametros*`, `prv_listas_tipos`, `prv_listas_elementos` | Parámetros y **catálogos genéricos (EAV)** |
| `prv_maestros_conceptos(_xref)`, `prv_maestros_documentos` | Definición de tipos de documento y conceptos |
| `prv_modulos`, `prv_modulos_objetos`, `prv_objetos`, `prv_parametros_secciones` | Registro de módulos/objetos para menú y permisos |
| `prv_correos_radian`, `prv_motivos_operaciones_especiales`, `prv_tipo_concepto_salud` | Integraciones DIAN/RADIAN y salud |
| `jwt_auth_session_storage` | Sesiones JWT |

⚠️ EAV dificulta integridad y reportes; mezcla configuración técnica con maestros de negocio.

---

## Nómina (`nom_`) — Liquidación de personal · 🔴

| Sub-área | Tablas |
|---|---|
| Estructura de cargos | `nom_cargos`, `nom_perfiles_cargos`, `nom_plan_cargos*`, `nom_tipos_vinculaciones` |
| Vinculaciones | `nom_vinculaciones`, `nom_vinculaciones_encabezados_documentos`, `nom_vinculaciones_renovaciones`, `nom_reubicaciones` |
| Conceptos | `nom_conceptos`, `nom_conceptos_bases`, `nom_cargos_conceptos`, `nom_conceptos_centros_costos` |
| Liquidación | `nom_procesos`, `nom_detalles_liquidaciones*`, `nom_novedades*`, `nom_cesantias_*`, `nom_embargos_descuentos`, `nom_depositos_judiciales` |
| Seguridad social (PILA) | `nom_pila_*` (archivos, cruces, detalle tipo 1/2, novedades) |
| Interfaces | `nom_detalles_contabilidad`, `nom_detalles_contables`, `nom_detalles_presupuesto` |
| Catedráticos (salud/u.) | `nom_tarifas_catedras`, `nom_carga_academica_academulsoft` |

⚠️ 311 migraciones; tablas `_temp`; integración académica y de salud incrustada; algunas
tablas de nómina se crean desde el módulo **Académico**.

---

## Académico (`aca_`) — Educación superior · 🟠

`aca_facultades`, `aca_departamentos`, `aca_programas_academicos`, `aca_asignaturas(_programas)`,
`aca_grupos_academicos`, `aca_cargas_academicas(_detalles/_novedades/_modificaciones)`,
`aca_tarifas_catedras`, `aca_tipos_docentes_catedras`, `aca_instituciones_snies`.

🧠 Gestión de oferta académica y **carga docente** (insumo de nómina de catedráticos).
⚠️ Crea `nom_detalles_liquidacion_temp` (tabla de nómina) → frontera difusa.

---

## Presupuesto (`pre_`) — Presupuesto público · 🔴

`pre_planes_presupuestales(_ingresos_gastos)`, `pre_detalles_presupuestales`, `pre_cpcs`,
`pre_fuentes_financiamientos(_proyectos/_planes)`, `pre_proyectos`, `pre_planes_financieros`,
`pre_pac_centros_costos`, `pre_saldos_*` (centros de costo, fuentes, terceros),
`pre_cierres_presupuestales`, `pre_interfaces_*` (nómina, tipos cliente).

🧠 Presupuesto orientado a **sector público** (CPC, fuentes de financiación, PAC, proyectos).
Mantiene **saldos** propios y se integra con nómina/facturación vía interfaces.

---

## Contabilidad (`con_`) + Costos (`cos_`) · 🔴

`con_plan_contable`, `con_conceptos_contables`, `con_detalles_contables`,
`con_saldos_*` (centros de costo, terceros, recursos, proyectos),
`con_cierres_contables`, `con_estados_financieros(*)`, `con_reclasificaciones_contables`,
`con_politicas`, `con_revelaciones`, `con_informacion_exogena(_detalles)`,
`con_conceptos_dian`, `con_formularios_dian`, `cos_recursos`.

🧠 Contabilidad por partida doble con **saldos materializados** por múltiples ejes
(tercero, centro de costo, recurso, proyecto), estados financieros, NIIF (revelaciones/políticas)
e información exógena DIAN. Es el **destino de las interfaces** de los demás módulos.

---

## Facturación (`fac_`) — Ingresos / salud · 🔴

`fac_facturas_electronicas`, `fac_detalles_facturacion`, `fac_recaudos`, `fac_recibos_academulsoft`,
`fac_tarifas`, `fac_manuales_tarifarios(_categorias)`, `fac_contratos*` (citas, copagos/cuotas,
metas, promoción/mantenimiento), `fac_tipos_clientes/servicios`, `fac_resoluciones_facturacion`,
`fac_factura_electronica_errores`, `fac_facturas_errores_dian`, `fac_firmas_digitales`,
`fac_excepciones_tarifarias_cirugias`, `fac_incremento_tarifas`.

🧠 Facturación electrónica DIAN + **facturación de salud** (contratos con EPS, copagos/cuotas
moderadoras, manuales tarifarios ISS/SOAT, cirugías, capitación). Integra con cartera y contabilidad.

---

## Inventario (`inv_`) + Quirúrgico (`cir_`) · 🟠

`inv_bodegas(_responsables/_abastecimiento)`, `inv_productos`/`inv_categorias`, `inv_lotes`,
`inv_saldos_productos`, `inv_detalles_inventarios`, `inv_ubicaciones`, `inv_marcas`, `inv_tarifas`,
`inv_reclamaciones`, `inv_cierres`, `cir_grupos_quirurgicos`, e interfaces
`inv_detalles_contabilidad/_presupuesto`.

⚠️ Crea `ctr_contratos` (también creada en Contratación) → **tabla duplicada entre módulos**.

---

## Compras (`cmp_`) + Contratación (`ctr_`) · 🟠

Compras: `cmp_planes_adquisiciones(_detalles)`, `cmp_estudios_previos_*`, `cmp_cuadros_comparativos_detalles`,
`cmp_comparativos_productos`, `cmp_requisitos_habitantes`, `com_encabezado_polizas_seguros`,
`com_detalles_polizas_seguros`.
Contratación: `ctr_contratos(_detalles)`, `ctr_modalidades`, `ctr_tipos_contratos_requisitos_habilitantes`,
`ctr_plantillas_clausulas`, `ctr_documentos_detalles`.

🧠 Ciclo de **contratación estatal**: plan de adquisiciones → estudios previos (riesgos,
ponderación) → cuadro comparativo → contrato (modalidad, cláusulas, pólizas). Pólizas viven
con prefijo `com_` (otra mezcla de frontera).

---

## Tesorería (`tes_`) · 🟠 · Caja (`caj_`) · 🟡

Tesorería: `tes_cajas(_responsables)`, `tes_cuentas_bancarias`, `tes_chequeras(_detalles/_operaciones/_plantillas)`,
`tes_conciliacion_bancaria`, `tes_extractos_bancarios(_detalles)`, `tes_detalles_comprobantes_egresos`,
`tes_detalles_giros`, `tes_detalles_cuentas_pagar`.
Caja: `caj_detalles_cajas`, `caj_detalles_recaudos`, `caj_detalles_recibos_caja`, interfaces contables/presup.

🧠 Tesorería: bancos, chequeras, conciliación, egresos/giros. Caja: recaudo en punto.

---

## Cartera (`cpc_`) · 🟡 · Cuentas por pagar (`cpp_`) · 🟡

Cartera: `cpc_detalles_cartera`, `cpc_detalles_acuerdos_pago`, `cpc_referencias_cartera`, interfaces.
CxP: `cpp_facturas_dian`, `cpp_eventos_facturas_dian`, `cpp_detalles_documentos_soportes`,
`cpp_detalles_deducibles_retefuente`, interfaces.

🧠 Cartera = saldos por cobrar y acuerdos de pago. CxP = recepción de facturas DIAN de
proveedores (RADIAN), documentos soporte y retenciones.

---

## Activos fijos (`acf_`) · 🟡

`acf_activos_fijos`, `acf_detalles_depreciaciones`, `acf_responsables_activos_fijos`,
`acf_polizas_seguros(_detalles)`, `acf_cierres`. → Ciclo de vida del activo y depreciación.

---

## Talento humano (`thu_`) · 🟡 · Disciplinarios (`jur_`) · ⚪

THU: `thu_empleados_estudios/_familiares/_historias_laborales/_idiomas`, `thu_dependencias`,
`thu_grupos(_usuarios/_detalles)`, `thu_evaluaciones(_respuestas)`, `thu_programas_evaluaciones*`,
`thu_niveles_estudios`, `thu_resultados_historicos`.
Jurídico: `jur_encabezados_procesos`, `jur_detalles_procesos_*` (faltas, descargos, notificaciones),
`jur_faltas`, `jur_clasificaciones_faltas`.

🧠 THU = hoja de vida y evaluación de desempeño. Jurídico = procesos disciplinarios internos.

---

## Administración (`adm_`) · ⚪ · Custom · ⛔ · Seguridad (User/Sentinel) · 🔴

- Administración: `adm_cierres_mes`, `adm_flujos_trabajos`, `audits_login`, `resset_password`.
- **Custom: NO es un módulo.** Recrea/altera tablas de otros (`com_subcentros`, `fac_tarifas`,
  `com_sedes_users`, `terceros_por_tipos`, `audits`). Es un "cajón de parches". ⛔
- Seguridad: Cartalyst **Sentinel** (`users`, `roles`, `activations`, `persistences`…) + **JWT**
  (`tymon/jwt-auth`) + AsgardCMS (User/Menu/Setting/Translation/Media/Page/Tag).

---

## Módulos sin tablas propias (capa de proceso o vacíos)

`Core`, `Autogestion`, `Autorizacion`, `Bienestar`, `Relacioneslaborales`,
`Evaluaciondesempeno`, `Workshop`, `Dashboard`. 🧠 Algunos son lógica/UI sobre tablas ajenas;
otros parecen **placeholders** o módulos abandonados.
