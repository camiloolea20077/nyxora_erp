# Fase 7 — Matriz de trazabilidad

> Mapea cada **elemento analizado** del ERP actual a su **función**, **problemas** y al
> **concepto equivalente + propuesta de diseño** en el nuevo ERP.
> Prioridad: P1 (núcleo, primero) · P2 (finanzas) · P3 (operación) · P4 (verticales/soporte).

| Elemento analizado (actual) | Función identificada | Módulo actual | Problemas | Concepto equiv. (nuevo) | Propuesta de diseño | Prio. | Observaciones |
|---|---|---|---|---|---|---|---|
| `com_encabezados_documentos` | Documento transaccional universal | Común | Tabla-Dios ~50 col., sin FK, nombres genéricos | `documento` + extensiones por tipo | Base mínima tipada + `*_ext` por dominio + líneas | P1 | Antipatrón central a eliminar |
| `com_documentos`, `com_documentos_consecutivos` | Tipos de documento y numeración | Común | Numeración por app sin atomicidad fuerte | `tipo_documento`, servicio de consecutivos | Consecutivo por (tipo,sede,vigencia) transaccional | P1 | Conservar concepto |
| `com_encabezados_documentos_workflow`, `adm_flujos_trabajos` | Aprobaciones | Común/Admin | Workflow acoplado al documento | Máquina de estados + workflow desacoplado | Estados enum + tabla de transiciones + eventos | P1 | |
| `com_terceros` (+contactos/correos/roles) | Maestro personas/entidades | Común | Identidad y rol mezclados | `tercero` + `tercero_rol` | Separar identidad de rol (cliente/proveedor/empleado/paciente) | P1 | Maestro único: buena idea |
| `com_productos` (+variantes/compuestos/dosis) | Catálogo bienes/servicios | Común | Atributos de salud (dosis) en núcleo | `producto`+`producto_variante`; dosis → módulo clínico | Catálogo tipado; verticales extienden | P1 | |
| `com_sedes`, `prv_empresas` | Multisede / multiempresa | Común/Privado | Config técnica y negocio mezcladas | `organizacion`, `sede` | Ciudadanos de primer nivel del núcleo | P1 | Fortaleza a conservar |
| `com_vigencias`, `com_apertura_vigencia*` | Año fiscal y apertura/cierre | Común | — | `vigencia` (+ estados) | Dimensión fiscal transversal | P1 | Fortaleza a conservar |
| `com_centros_costos`, `com_subcentros`, `com_proyectos` | Ejes de costo | Común | `com_subcentros` duplicada en Custom | `centro_costo`, `proyecto` | Jerarquía con FK; sin duplicar | P1 | |
| `prv_listas_tipos/_elementos`, `prv_maestros_conceptos` | Catálogos genéricos | Privado | EAV, sin integridad | `lista`/`lista_item` tipadas | Catálogos por dominio con FK | P1 | Reemplazar EAV |
| `com_*_formularios_dinamicos`, `com_preguntas/respuestas*` | Formularios dinámicos | Común | EAV de UI/datos | Motor de formularios desacoplado | JSONB documentado + esquema validable | P2 | Solo donde sea realmente dinámico |
| `con_plan_contable`, `con_conceptos_contables` | Plan único de cuentas | Contabilidad | — | `cuenta` | Plan jerárquico con FK | P2 | |
| `con_detalles_contables`, `*_detalles_contabilidad` | Asientos / interfaces contables | Todos | Doble tabla (`_contables`/`_contabilidad`), saldos descuadrables | `comprobante`+`movimiento` (append-only) | Movimientos inmutables; interfaces vía eventos | P2 | Unificar |
| `con_saldos_*` (CC, tercero, recurso, proyecto) | Saldos por ejes | Contabilidad | Fuente de verdad duplicada | `saldo_proyeccion` | Proyección reconstruible desde movimientos | P2 | Concepto útil, implementación nueva |
| `con_estados_financieros*`, `con_informacion_exogena*`, `con_conceptos_dian` | EEFF, exógena, DIAN | Contabilidad | — | Reportería contable + exógena | Módulo de reportes sobre proyecciones | P2 | NIIF/DIAN obligatorio |
| `pre_planes_presupuestales*`, `pre_detalles_presupuestales`, `pre_cpcs` | Estructura presupuestal | Presupuesto | — | `plan_presupuestal`, `rubro`, `cpc` | Cadena CDP→Comp→Oblig→Pago | P2 | Sector público |
| `pre_saldos_*`, `pre_pac_centros_costos` | Saldos y PAC | Presupuesto | Saldos descuadrables | `afectacion_presupuestal` + proyección | Disponibilidad validada por evento | P2 | |
| `pre_fuentes_financiamientos*` | Fuentes de financiación | Presupuesto | — | `fuente_financiamiento` | FK a rubro/proyecto | P2 | |
| `tes_cuentas_bancarias`, `tes_chequeras*`, `tes_conciliacion_bancaria`, `tes_extractos*` | Bancos, cheques, conciliación | Tesorería | — | `cuenta_bancaria`, `egreso`, `conciliacion` | Estados + conciliación por matching | P2 | |
| `tes_detalles_comprobantes_egresos`, `tes_detalles_giros` | Egresos/giros | Tesorería | Acoplado a documento universal | `egreso` (documento propio) | Evento `PagoRealizado` | P2 | |
| `caj_detalles_recaudos/_recibos_caja` | Recaudo en punto | Caja | — | `recibo_caja`, `arqueo` | Estados + traslado a tesorería | P2 | |
| `cpc_detalles_cartera`, `cpc_detalles_acuerdos_pago`, `com_edades_cuentas` | Cartera y edades | Cartera | Edades calculadas ad hoc | `cuenta_por_cobrar`, `acuerdo_pago`, edad=proyección | Proyección desde facturas/recaudos | P2 | |
| `cpp_facturas_dian`, `cpp_eventos_facturas_dian`, `prv_correos_radian` | Recepción FE proveedor (RADIAN) | CxP/Privado | — | `factura_proveedor`, `evento_dian` | Integración DIAN desacoplada | P2 | |
| `cpp_detalles_deducibles_retefuente` | Retenciones | CxP | — | `retencion` | Catálogo tipado de retenciones | P2 | |
| `cmp_planes_adquisiciones*`, `cmp_estudios_previos_*`, `cmp_cuadros_comparativos*` | Ciclo de compra estatal | Compras | — | `plan_adquisicion`, `estudio_previo`, `cuadro_comparativo` | Flujo con estados | P3 | |
| `ctr_contratos(_detalles)`, `ctr_modalidades`, `ctr_plantillas_clausulas` | Contratación | Contratación | **Duplicada** (Inv. y Contrat.) | `contrato`, `clausula`, `modalidad` | Tabla única en Contratación | P3 | Resolver duplicado |
| `com_*_polizas_seguros` | Pólizas de contrato | Compras (prefijo com_) | Frontera violada | `contrato_poliza` | Vive en Contratación | P3 | |
| `inv_bodegas*`, `inv_lotes`, `inv_saldos_productos`, `inv_detalles_inventarios` | Inventario y kardex | Inventario | Saldos descuadrables | `bodega`, `lote`, `movimiento_inventario`, `kardex`(proy.) | Movimientos append-only | P3 | |
| `cir_grupos_quirurgicos` | Grupos quirúrgicos | Inventario | Salud en módulo equivocado | `grupo_quirurgico` | Módulo clínico | P4 | |
| `acf_activos_fijos`, `acf_detalles_depreciaciones`, `acf_polizas_seguros` | Activos y depreciación | Activos fijos | — | `activo`, `depreciacion`, `poliza` | Ciclo de vida + cálculo periódico | P3 | |
| `fac_facturas_electronicas`, `fac_detalles_facturacion`, `fac_resoluciones_facturacion` | Facturación electrónica | Facturación | — | `factura`, `resolucion_dian` | Estados + integración DIAN | P3 | |
| `fac_tarifas`, `fac_manuales_tarifarios*`, `fac_incremento_tarifas` | Tarifas | Facturación | `fac_tarifas` recreada en Custom | `tarifa`, `manual_tarifario` | Versionado por vigencia | P3 | |
| `fac_contratos*` (copagos, cuotas, citas, metas, capitación) | Contratos de salud | Facturación | Salud mezclada | `contrato_eps`, `copago`, `capitacion` | Módulo clínico/salud | P4 | |
| `nom_procesos`, `nom_detalles_liquidaciones*`, `nom_conceptos*`, `nom_novedades*` | Liquidación de nómina | Nómina | Tablas `_temp`; creada desde Académico | `proceso_nomina`, `concepto`, `novedad`, `liquidacion` | Cálculo idempotente; sin temporales persistentes | P3 | |
| `nom_pila_*` | Seguridad social (PILA) | Nómina | — | `aporte_seguridad_social` | Generación PILA estándar | P3 | |
| `aca_programas_academicos`, `aca_cargas_academicas*`, `aca_tarifas_catedras` | Oferta y carga docente | Académico | Crea tablas de nómina | `programa`, `carga_docente` | Emite evento a Nómina | P4 | Vertical universidad |
| `thu_empleados_*`, `thu_evaluaciones*` | Hoja de vida y desempeño | Talento humano | — | `empleado`, `hoja_vida`, `evaluacion` | Integra con Nómina por evento | P3 | |
| `jur_encabezados_procesos`, `jur_detalles_procesos_*`, `jur_faltas` | Procesos disciplinarios | Disciplinarios | — | `proceso_disciplinario`, `falta` | Submódulo de Talento Humano | P4 | |
| `con_cierres_contables`, `pre_cierres_presupuestales`, `adm_cierres_mes`, `inv_cierres`, `acf_cierres` | Cierres de periodo | Varios | Lógica de cierre dispersa | Servicio de cierre por vigencia | Orquestador de cierre transversal | P2 | Unificar |
| `audits`, `audits_login`, `*_procesos_logs`, `*_errores` | Auditoría y logs | Varios | Cobertura parcial (150/369) | Auditoría universal + event store | Trazabilidad obligatoria en todo | P1 | |
| Sentinel (`users`,`roles`,…) + JWT | Seguridad/identidad | User/Core | Permisos por id EAV | `Identidad y Acceso` | RBAC/ABAC explícito | P1 | |
| Módulo `Custom` (216 migr.) | Parches globales | Custom | Acoplamiento, viola fronteras | — (no existe) | Cambios dentro del módulo dueño | P1 | Eliminar el concepto |
| Módulos vacíos (`Bienestar`, `Autogestion`, `Relacioneslaborales`, `Workshop`…) | Placeholders | Varios | Sin tablas | Evaluar necesidad | Crear solo si hay dominio real | P4 | |
