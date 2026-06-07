# Mapeo ERP de referencia → Nyxora (esquema `public`, 390 tablas)

> Fuente: export real de la BD viva (`docs/base-datos/` 1/2/3.txt → columnas, FKs, vista compacta).
> Principio: **conservar todos los campos de negocio reales**, pero con **nombres limpios**, FK reales
> y **sin** la tabla-Dios / EAV / mezcla de dominios. Lo **clínico** se pospone a un vertical de salud.

## 1. Inventario por módulo (390 tablas)
| Prefijo | # | ¿MVP? |
|---|---|---|
| com (núcleo: terceros, productos, documentos, vigencias, impuestos…) | 91 | **Sí** (subconjunto) |
| fac (facturación) | 29 | **Sí** (subconjunto) |
| con (contabilidad) | 21 | **Sí** (básica) |
| inv (inventario) | 15 | **Sí** |
| cmp (compras) + ctr (contratación) | 14 | **Sí** (compras) |
| cpc (cartera) | 5 | **Sí** |
| caj (caja) | 5 | **Sí** |
| cos (costos) 1 · cir (quirúrgico) 1 · adm 2 | 4 | parcial |
| nom 60 · pre 19 · thu 16 · tes 15 · prv 15 · aca 11 · cpp 6 · acf 6 · jur 6 | 154 | **No** (fases posteriores) |
| his.* (historia clínica), formularios dinámicos, Sentinel/CMS, jobs… | resto | **No** / soporte |

## 2. Convención de renombres
- Quitar prefijos de módulo (`com_`, `fac_`…): el módulo se expresa por paquete/carpeta, no por nombre.
- Singular para entidades (`tercero`, no `terceros`); satélites como `<entidad>_<cosa>` (`tercero_contacto`).
- `numero_documento`/`tipo_documento_id` de identidad **chocan** con el motor de documentos: en Nyxora
  la identidad usa `tipo_identificacion`/`numero_documento`, y el documento transaccional usa `tipo_documento`.
- `*_id` → FK reales a catálogos limpios. `smallint (0/1)` → `BOOLEAN`. `json` de listas → tabla satélite.

## 3. Renombres propuestos — módulo Común (MVP)  *(ajusta lo que quieras)*
| Referencia (`public`) | Nyxora | Notas |
|---|---|---|
| com_terceros | **tercero** | subconjunto comercial/fiscal (ver §4) |
| com_terceros_contactos | **tercero_contacto** | |
| com_terceros_por_tipos | **tercero_clasificacion** | clasificación por `tipo_tercero` (cliente/proveedor/…) |
| (campos banco_1/2 inline) | **tercero_cuenta_bancaria** | normaliza las 2 cuentas inline a satélite |
| com_terceros_huellas / _dispositivos / _historico_documento_nombre | → vertical salud / auditoría | fuera del MVP |
| com_productos | **producto** | + variantes/compuestos como satélites |
| com_productos_variantes(_atributos) | **producto_variante** / **producto_variante_atributo** | |
| com_productos_proveedores | **producto_proveedor** | |
| com_productos_dosis / _esquemas / _calidad | → vertical salud | fuera del MVP |
| com_centros_costos (+ categorias) | **centro_costo** | jerárquico |
| com_proyectos | **proyecto** | |
| com_sedes / com_dependencias | **sede** / **dependencia** | (sede ya existe en V1) |
| com_vigencias / com_aperturas_vigencias(_detalles) | **vigencia** / **vigencia_apertura(_detalle)** | (vigencia ya en V1) |
| com_impuestos_deducciones (+ _vigencias/_categorias/_actividad) | **impuesto** (+ versiones por vigencia) | |
| com_documentos / com_documentos_consecutivos | **tipo_documento** / **consecutivo** | (ya en V2) |
| com_encabezados_documentos (tabla-Dios) | **documento** + extensiones por tipo | NO copiar las ~50 col; tipar |
| com_encabezados_documentos_* (workflow, referencias, impuestos, entrega…) | satélites tipados del documento | |
| condiciones/formas de pago, bancos, géneros, municipios, actividad económica (CIIU), tipo contribuyente | **catálogos** (`lista`/tablas propias) | hoy son `*_id` sin catálogo limpio |

> Otros módulos (Compras, Inventario, Facturación, Caja, Cartera, Contabilidad) se mapean igual,
> uno por uno, cuando los abordemos.

## 4. `tercero`: qué se conserva y qué se pospone
El `com_terceros` real tiene **~100 columnas** mezclando comercial + clínico.

### 4.1 SE CONSERVA en Nyxora `tercero` (comercial/fiscal)
Identidad: `tipo_identificacion_id` (FK), `numero_documento`, `digito_verificacion`,
`primer_nombre/segundo_nombre/primer_apellido/segundo_apellido` (← nombre1/2, apellido1/2),
`razon_social`, `nombre_comercial` (← razon_comercial), `nombre_representante_legal`,
`documento_representante_legal`, `tipo_persona` (← es_empresa), `codigo`, `nombre` (normalizado).
Personal mínimo: `genero_id`, `estado_civil_id`, `fecha_nacimiento`.
Ubicación: `municipio_id` (← municipio_residencia_id), `barrio_id`, `direccion`, `sitio_web`,
`fecha_expedicion_documento`, `municipio_expedicion_id`, `fecha_vencimiento_documento`.
Fiscal/DIAN: `actividad_economica_id` (CIIU), `tipo_contribuyente_id`, `responsable_iva`,
`es_autoretenedor_iva/_ica/_fuente`, `declarante`, `aplica_art_383`, `rut`, `obligacion_dian` (jsonb).
Comercial: `condicion_pago_cliente_id`, `condicion_pago_proveedor_id`, `forma_pago_cliente_id`,
`forma_pago_proveedor_id`, `interes_efectivo_mensual`, `cuenta_contable_proveedor_id`
(← con_plan_contable_proveedor_id), `recurso_id` (← cos_recurso_id), `es_reciproco`, `codigo_reciproco`.
Satélites: `tercero_contacto`, `tercero_direccion` (← otras_direcciones json), `tercero_cuenta_bancaria`
(← banco_1/2 inline), `tercero_clasificacion` (← com_terceros_por_tipos).

### 4.2 SE POSPONE (→ vertical salud / talento humano)
`rh_id, huella, *_libreta, tarjeta_profesional, pertenencia_etnica_id, nivel_educativo_id,
discapacidades, capacidades_excepcionales, exclusion_social, municipio_nacimiento_id, sexo_id,
orientacion_sexual_id, identidad_genero_id, ocupacion_id, es_cabeza_familia, aseguradora_id,
grupo_atencion_id, *_sisben*, discapacidad_id, parentesco_id, nombre_padre/madre, fac_contrato_id,
es_asistencial, especialidad_id, perfil_asistencial_id, administrador_sivigila, persona_atiende_id,
registro_profesional, fac_manual_tarifario_id, cgn_id, tipo_afiliacion_id, hobbies_id,
tipo_evaluador_id, preinscrito, lote, responsable_activos, com_tipo_actividad_otras`.

## 5. Catálogos a crear (hoy son `*_id` sin tabla limpia)
`tipo_identificacion`, `genero`, `estado_civil`, `municipio`(+`departamento`,`pais`,`barrio`) DANE,
`actividad_economica` (CIIU), `tipo_contribuyente`, `condicion_pago`, `forma_pago`, `banco`,
`tipo_cuenta_bancaria`, `tipo_tercero`. (Algunos pueden resolverse con `lista`/`lista_item`.)

## 6. Pendiente de confirmar contigo
1. ¿OK conservar comercial/fiscal y **posponer lo clínico** (§4.2) al vertical salud?
2. ¿Apruebas la **convención de renombres** (§2) y los nombres de §3? ¿Cambias alguno?
3. ¿Catálogos como **tablas propias** (más limpio/escalable) o vía `lista`/`lista_item` (genérico)?
4. **Orden de módulos** a construir tras Común: ¿Compras→Inventario→Facturación→Cartera→Caja→Contabilidad?
