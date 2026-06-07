# Diccionario de datos — Nyxora ERP

> Resumen legible del esquema (PostgreSQL, `public`). **Fuente de verdad real:** las migraciones
> `src/main/resources/db/migration/V*.sql`. Si discrepan, gana la migración (y se actualiza aquí).
> Todas las tablas de negocio incluyen auditoría estándar salvo las marcadas (global/append-only/asociación).

**Auditoría estándar (en cada tabla de negocio):**
`created_at TIMESTAMP NOT NULL DEFAULT now()`, `updated_at TIMESTAMP`, `deleted_at TIMESTAMP`,
`usuario_creacion BIGINT`, `usuario_modificacion BIGINT`, `activo BOOLEAN NOT NULL DEFAULT TRUE`.

---

## Módulo Administración (V1)

### empresa  *(tenant raíz; SIN empresa_id)*
| columna | tipo | notas |
|---|---|---|
| id | bigint PK identity | |
| nit | varchar(20) | UNIQUE |
| razon_social | varchar(255) | |
| tipo_persona | varchar(10) | CHECK natural\|juridica |
| activo + auditoría (sin usuario_*? sí los lleva) | | |

### sede
| columna | tipo | notas |
|---|---|---|
| id | bigint PK | |
| empresa_id | bigint NOT NULL | FK empresa |
| codigo | varchar(20) | UNIQUE(empresa_id, codigo) |
| nombre | varchar(255) | |

### usuario
| id PK · empresa_id FK · username varchar(60) UNIQUE(empresa_id,username) · email varchar(255) · hash_password varchar(255) |

### rol
| id PK · empresa_id FK · nombre varchar(100) UNIQUE(empresa_id,nombre) |

### permiso  *(catálogo GLOBAL; sin empresa_id ni auditoría)*
| id PK · codigo varchar(100) UNIQUE · descripcion varchar(255) |

### rol_permiso  *(asociación; sin auditoría)*
| rol_id FK · permiso_id FK · PK(rol_id, permiso_id) |

### usuario_rol  *(asociación)*
| usuario_id FK · rol_id FK · sede_id FK · PK(usuario_id, rol_id, sede_id) |

### vigencia
| id PK · empresa_id FK · anio int · estado varchar(15) CHECK(planeada\|abierta\|en_cierre\|cerrada) · fecha_apertura date · fecha_cierre date · UNIQUE(empresa_id, anio) |

### parametro
| id PK · empresa_id FK · clave varchar(100) UNIQUE(empresa_id,clave) · valor varchar(500) · tipo_dato varchar(20) CHECK(string\|int\|decimal\|bool\|date) |

### auditoria  *(append-only; sin updated_at/deleted_at)*
| id PK · empresa_id · usuario_id · entidad varchar(100) · entidad_id bigint · accion varchar(20) CHECK(crear\|actualizar\|eliminar\|anular) · valores_antes jsonb · valores_despues jsonb · created_at |

---

## Módulo Común (V2)

> **Catálogos en V4** (`comun_catalogos`, globales sin empresa_id): `tipo_identificacion`, `genero`,
> `estado_civil`, `tipo_tercero`, `tipo_contribuyente`, `actividad_economica` (CIIU), `condicion_pago`,
> `forma_pago`, `banco`, `tipo_cuenta_bancaria`, y geografía `pais → departamento → municipio → barrio`.

### tercero  (V5, mapeado del com_terceros real — subconjunto comercial/fiscal)
Identificación: `tipo_identificacion_id FK`, `numero_documento`, `digito_verificacion smallint`,
`tipo_persona CHECK(natural|juridica)`, `codigo`. · UNIQUE(empresa_id, tipo_identificacion_id, numero_documento).
Natural: `primer_nombre, segundo_nombre, primer_apellido, segundo_apellido`. ·
Jurídica: `razon_social, nombre_comercial, nombre_representante_legal, documento_representante_legal`. ·
`nombre` (normalizado). · Personal: `genero_id FK, estado_civil_id FK, fecha_nacimiento`. ·
Ubicación: `municipio_id FK, barrio_id FK, direccion, sitio_web`. ·
Documento: `fecha_expedicion_documento, municipio_expedicion_id FK, fecha_vencimiento_documento`. ·
Fiscal/DIAN: `actividad_economica_id FK, tipo_contribuyente_id FK, responsable_iva, es_autoretenedor_iva/_ica/_fuente,
declarante, aplica_art_383, tiene_rut, obligacion_dian jsonb`. ·
Comercial: `condicion_pago_cliente_id FK, condicion_pago_proveedor_id FK, forma_pago_cliente_id FK,
forma_pago_proveedor_id FK, interes_efectivo_mensual numeric(7,4), cuenta_contable_proveedor_id (FK futura),
recurso_id (FK futura), es_reciproco, codigo_reciproco`. · `metadatos jsonb, observaciones`. · + auditoría estándar.
Índices: empresa_id, (empresa_id,numero_documento), (empresa_id,nombre).
> POSPUESTO a vertical salud/RRHH (no migrado): rh, huella, libreta, sisben, discapacidad, aseguradora,
> perfil_asistencial, sivigila, manual_tarifario, etc. (ver `docs/base-datos/mapeo-referencia-nyxora.md §4.2`).

### tercero_clasificacion  *(← com_terceros_por_tipos)*
| id PK · tercero_id FK · tipo_tercero_id FK · activo · UNIQUE(tercero_id, tipo_tercero_id) |

### tercero_contacto  *(satélite, ← com_terceros_contactos)*
| id PK · tercero_id FK · nombre · cargo · telefono · celular · email · notas · principal bool · activo |

### tercero_direccion  *(satélite, ← otras_direcciones)*
| id PK · tercero_id FK · tipo CHECK(principal\|facturacion\|envio) · direccion · municipio_id FK · barrio_id FK · codigo_postal · telefono · principal bool · activo |

### tercero_cuenta_bancaria  *(satélite, ← banco_1/2 inline normalizado)*
| id PK · tercero_id FK · banco_id FK · tipo_cuenta_bancaria_id FK · numero_cuenta · principal bool · activo |

### unidad_medida  *(catálogo GLOBAL; sin empresa_id)*
| id PK · codigo varchar(10) UNIQUE · nombre varchar(100) |

### categoria  (V6, ← com_categorias; jerárquica nested set)
| id PK · empresa_id FK · categoria_padre_id FK · codigo UNIQUE(empresa_id,codigo) · nombre · tipo_producto · metodo_costeo · izquierda/derecha/nivel · activo · (config contable con_cuenta_* → fase Contabilidad) |

### producto  (V6, ← com_productos; subconjunto comercial/logístico)
| id PK · empresa_id FK · categoria_id FK · codigo UNIQUE(empresa_id,codigo) · codigo_unspsc · nombre · descripcion · tipo CHECK(bien\|servicio) · es_compuesto · unidad_mayor_id FK · unidad_menor_id FK · contenido · maneja_inventario · maneja_lote · maneja_desperdicio · es_devolutivo · stock_minimo/maximo · tiempo_reabastecimiento · impuesto_id FK · discrimina_iva · aplica_impuesto_bolsa · tarifa_maxima · es_pos · recurso_id (FK futura) · imagen jsonb · + auditoría |
> POSPUESTO a salud (no migrado): cups/soat/iss, forma_farmaceutica, via_administracion, posologia, medicamento_control, finalidades, concentracion, etc.

### producto_variante  (V6, ← com_productos_variantes)
| id PK · producto_id FK · sku_plu · codigo_barra(EAN-13) · precio_adicional · costo · imagen jsonb · activo |

### producto_proveedor  (V6, ← com_productos_proveedores)
| id PK · producto_id FK · proveedor_id FK→tercero · codigo_producto · cantidad_minima · plazo_entrega · UNIQUE(producto_id, proveedor_id) |

### impuesto  (V2, ← com_impuestos_deducciones)
| id PK · empresa_id FK · codigo · nombre · tipo CHECK(iva\|retencion\|ica\|otro) · causacion · base_gravable · periodicidad · aplica_aiu · retencion_nomina · tarifa numeric(7,4) · vigencia_id FK · cuenta_compra_id/cuenta_venta_id (FK futura) · UNIQUE(empresa_id, codigo, vigencia_id) |

### producto_impuesto  *(asociación, V6)*
| producto_id FK · impuesto_id FK · PK(producto_id, impuesto_id) |

### centro_costo  (V7, ← com_centros_costos; jerárquico nested set)
| id PK · empresa_id FK · sede_id FK · centro_costo_padre_id FK · codigo UNIQUE(empresa_id,codigo) · nombre · tipo_centro_costo · clase_centro_costo · es_observacion(hoja imputable) · maneja_plan_financiero · tercero_id FK · direccion · unidad_negocio_id · izquierda/derecha/nivel · activo |

### dependencia  (V7, ← com_dependencias; jerárquica)
| id PK · empresa_id FK · centro_costo_id FK · dependencia_padre_id FK · codigo UNIQUE(empresa_id,codigo) · nombre · ubicacion · latitud · longitud · izquierda/derecha/nivel · activo |

### proyecto  (V7, ← com_proyectos)
| id PK · empresa_id FK · codigo UNIQUE(empresa_id,codigo) · nombre · descripcion · programa_id · fecha_inicio · fecha_final · activo |

### lista
| id PK · empresa_id FK · codigo varchar(50) UNIQUE(empresa_id,codigo) · nombre varchar(255) |

### lista_item
| id PK · lista_id FK · codigo varchar(50) · valor varchar(255) · orden int · activo · UNIQUE(lista_id, codigo) |

### tipo_documento
| id PK · empresa_id FK · modulo varchar(30) · codigo varchar(20) UNIQUE(empresa_id,codigo) · nombre varchar(255) · prefijo varchar(10) · reinicia_por_vigencia bool |

### consecutivo  *(numeración transaccional)*
| id PK · tipo_documento_id FK · sede_id FK · vigencia_id FK · ultimo_numero bigint · UNIQUE(tipo_documento_id, sede_id, vigencia_id) |

---

---

## Módulo Contabilidad básica (V8)

### cuenta  (← con_plan_contable; plan jerárquico nested set)
| id PK · empresa_id FK · cuenta_padre_id FK · codigo_cuenta(15) UNIQUE(empresa_id) · nombre_cuenta(200) · nivel · izquierda/derecha · naturaleza CHECK(debito\|credito) · tipo_cuenta · maneja_movimiento(hoja) · maneja_movimiento_manual · maneja_tercero/_centro_costo/_impuesto/_proyecto/_recurso · maneja_saldo_contrario · es_corriente · activo |

### periodo_contable
| id PK · empresa_id FK · vigencia_id FK · anio · mes CHECK(1..12) · estado CHECK(abierto\|cerrado) · fecha_cierre · UNIQUE(empresa_id, anio, mes) |

### comprobante  *(encabezado del asiento)*
| id PK · empresa_id FK · periodo_contable_id FK · tipo_documento_id FK · numero · fecha · descripcion · estado CHECK(borrador\|confirmado\|reversado) · total_debito · total_credito · origen_modulo/origen_id (traza) |

### movimiento_contable  *(← con_detalles_contables; APPEND-ONLY)*
| id PK · empresa_id FK · comprobante_id FK · cuenta_id FK · tercero_id FK · centro_costo_id FK · proyecto_id FK · recurso_id · descripcion · debito · credito · valor_base · impuesto_id FK · porcentaje_impuesto · valor_trm · valor_dolar · created_at (sin updated/deleted) |

### saldo_contable  *(← con_saldos_*; PROYECCIÓN recalculable)*
| id PK · empresa_id FK · periodo_contable_id FK · cuenta_id FK · tercero_id FK · centro_costo_id FK · saldo_debito/credito_anterior · debito/credito_periodo · saldo_debito/credito_final · UNIQUE(periodo, cuenta, tercero, centro_costo) |

---

---

## Módulo Inventario (V9)
### marca  *(← inv_marcas)* | id PK · empresa_id FK · codigo UNIQUE(empresa_id) · nombre · activo |
### bodega  *(← inv_bodegas)*
| id PK · empresa_id FK · sede_id FK · centro_costo_id FK · codigo UNIQUE(empresa_id) · nombre · tipo_abastecimiento · direccion · latitud/longitud · permite_compra · activo |
### bodega_responsable | id PK · bodega_id FK · tercero_id FK · predeterminado · activo |
### ubicacion  *(← inv_ubicaciones; jerárquica)*
| id PK · empresa_id FK · bodega_id FK · ubicacion_padre_id FK · codigo UNIQUE(bodega_id) · nombre · pasillo/altura/posicion · izq/der/nivel · activo |
### lote  *(← inv_lotes)*
| id PK · empresa_id FK · producto_variante_id FK · codigo UNIQUE(empresa_id) · nombre · fecha_fabricado · fecha_vencimiento · activo |
### movimiento_inventario  *(← inv_detalles_inventarios; APPEND-ONLY)*
| id PK · empresa_id FK · bodega_id FK · ubicacion_id FK · producto_id FK · producto_variante_id FK · lote_id FK · tipo CHECK(entrada\|salida\|ajuste\|traslado) · fecha · cantidad · costo_unitario · descuento_% /valor · impuesto_id FK · impuesto_% /valor · subtotal · total · centro_costo_id FK · tercero_id FK · origen_modulo/origen_id · created_at |
### saldo_inventario  *(← inv_productos_saldos; PROYECCIÓN recalculable)*
| id PK · empresa_id FK · bodega_id FK · ubicacion_id FK · lote_id FK · producto_id FK · producto_variante_id FK · cantidad · costo_promedio · valor_total · UNIQUE(bodega,ubicacion,lote,variante) |

## Módulo Compras (V10)  *(orden_compra limpia; el real no la tenía)*
### orden_compra
| id PK · empresa_id FK · sede_id FK · vigencia_id FK · tipo_documento_id FK · numero · proveedor_id FK→tercero · bodega_id FK · centro_costo_id FK · condicion_pago_id FK · fecha · fecha_entrega · estado CHECK(borrador\|aprobada\|recibida_parcial\|recibida_total\|cerrada\|anulada) · subtotal/descuento/impuestos/total |
### orden_compra_linea
| id PK · orden_compra_id FK · producto_id FK · producto_variante_id FK · descripcion · cantidad · unidad_medida_id FK · valor_unitario · descuento_% /valor · impuesto_id FK · impuesto_% /valor · subtotal · total · cantidad_recibida · cantidad_pendiente · centro_costo_id FK |
### recepcion
| id PK · empresa_id FK · orden_compra_id FK · bodega_id FK · tipo_documento_id FK · numero · fecha · estado CHECK(borrador\|confirmada\|anulada) |
### recepcion_linea
| id PK · recepcion_id FK · orden_compra_linea_id FK · producto_id FK · producto_variante_id FK · lote_id FK · ubicacion_id FK · cantidad_recibida · costo_unitario |

---

---

## Módulo Facturación (V11)
### resolucion_dian  *(← fac_resoluciones_facturacion)*
| id PK · empresa_id FK · numero_resolucion UNIQUE(empresa_id) · prefijo · factura_inicial/final · fecha_inicial/final · clave_tecnica · consecutivo_actual · activo |
### factura
| id PK · empresa_id FK · sede_id FK · vigencia_id FK · tipo_documento_id FK · resolucion_dian_id FK · numero · cliente_id FK→tercero · bodega_id FK · centro_costo_id FK · condicion_pago_id FK · fecha · fecha_vencimiento · estado CHECK(borrador\|emitida\|anulada) · subtotal/descuento/impuestos/total |
### factura_linea  *(← fac_detalles_facturacion)*
| id PK · factura_id FK · producto_id FK · producto_variante_id FK · descripcion · cantidad · unidad_medida_id FK · valor_unitario · descuento_% /valor · subtotal · impuesto_id FK · porcentaje_impuesto · valor_impuesto · discrimina_iva · total · bodega_id FK · lote_id FK · centro_costo_id FK |
### factura_dian  *(← fac_facturas_electronicas)*
| id PK · factura_id FK UNIQUE · cufe · estado_dian · fecha_acuse · comentario_acuse |

## Módulo Cartera (V12)
### cuenta_por_cobrar  *(← cpc_detalles_cartera)*
| id PK · empresa_id FK · cliente_id FK · factura_id FK · cuenta_id FK · fecha_emision · fecha_vencimiento · dias · valor_total · valor_interes · saldo · fecha_ultima_liquidacion · estado CHECK(vigente\|en_acuerdo\|pagada\|anulada) |
### acuerdo_pago  *(← cpc_detalles_acuerdos_pago)*
| id PK · empresa_id FK · cuenta_por_cobrar_id FK · fecha · numero_cuotas · estado CHECK(vigente\|cumplido\|incumplido\|anulado) |
### acuerdo_pago_cuota
| id PK · acuerdo_pago_id FK · numero_cuota · valor · fecha_aplicacion · estado CHECK(pendiente\|pagada\|vencida) · UNIQUE(acuerdo, numero_cuota) |

## Módulo Caja (V13)
### caja
| id PK · empresa_id FK · sede_id FK · usuario_id FK · codigo UNIQUE(empresa_id) · nombre · estado CHECK(abierta\|cerrada) · saldo_inicial · fecha_apertura/cierre |
### recibo_caja
| id PK · empresa_id FK · caja_id FK · tipo_documento_id FK · numero · cliente_id FK · fecha · valor · estado CHECK(registrado\|anulado) |
### recibo_caja_pago  *(← caj_detalles_recaudos; medios de pago)*
| id PK · recibo_caja_id FK · forma_pago_id FK · valor · banco_id FK · numero_cheque · numero_tarjeta · cuenta_bancaria |
### recibo_caja_linea  *(← caj_detalles_recibos_caja; aplicación a CxC)*
| id PK · recibo_caja_id FK · cuenta_por_cobrar_id FK · valor_aplicado |
### arqueo
| id PK · empresa_id FK · caja_id FK · fecha · valor_declarado · valor_sistema · diferencia · observaciones |

---

---

## Módulo Costos (V14)
### recurso  *(← cos_recursos)* | id PK · empresa_id FK · codigo UNIQUE · nombre · tipo_recurso · driver · costo_adicional · descripcion · activo |

## Módulo Presupuesto (V15)
### fuente_financiamiento *(← pre_fuentes_financiamientos)* | id PK · empresa_id FK · codigo · nombre · tipo_recurso |
### cpc *(← pre_cpcs; jerárquico)* | id PK · empresa_id FK · vigencia_id FK · cpc_padre_id FK · codigo · nombre · maneja_movimiento |
### rubro_presupuestal *(← pre_planes_presupuestales; definición, nested set)* | id PK · empresa_id FK · vigencia_id FK · rubro_padre_id FK · tipo_rubro(ingreso\|gasto) · codigo_rubro · nombre_rubro · maneja_movimiento · izq/der/nivel |
### afectacion_presupuestal *(← pre_detalles_presupuestales; APPEND-ONLY)* | id PK · rubro FK · tipo_operacion CHECK(disponibilidad\|compromiso\|obligacion\|pago\|reconocimiento\|recaudo) · tercero/centro_costo/proyecto/fuente/cpc FK · valor · saldo · origen_modulo/id |
### saldo_presupuestal *(← agregados; PROYECCIÓN)* | rubro FK · anio · mes · plan_inicial · adiciones · reducciones · disponibilidad · compromiso · obligacion · pagado · recaudos · UNIQUE(rubro,anio,mes) |
### pac_presupuestal *(← pac01..12)* | rubro FK · anio · mes(1..12) · valor |

## Módulo Tesorería (V16)
### cuenta_bancaria *(propia empresa, ← tes_cuentas_bancarias)* | id PK · empresa_id FK · banco_id FK · tipo_cuenta_bancaria_id FK · numero_cuenta · cuenta_contable_id FK · maneja_sobregiro · acepta_transferencias |
### chequera *(← tes_chequeras)* | id PK · cuenta_bancaria_id FK · numero_inicial/final · consecutivo_actual |
### comprobante_egreso | id PK · empresa_id FK · cuenta_bancaria_id FK · beneficiario_id FK→tercero · forma_pago_id FK · numero · fecha · valor · estado CHECK(borrador\|girado\|conciliado\|anulado) · origen_modulo/id |
### extracto_bancario *(← tes_extractos_bancarios)* + extracto_bancario_detalle | cuenta_bancaria_id FK · fechas · saldos · detalle(fecha, valor, tipo, conciliado) |
### conciliacion_bancaria *(← tes_conciliacion_bancaria)* | cuenta_bancaria_id FK · extracto_detalle_id FK · cuenta_contable_id FK · valor_conciliado |

---

## Módulo Cuentas por Pagar (V17)
### factura_proveedor *(← cpp_facturas_dian)* | id PK · empresa_id FK · proveedor_id FK · receptor_id FK · numero_documento · cufe · fecha_recepcion · valor_factura · xml/pdf · estado |
### factura_proveedor_evento *(← cpp_eventos_facturas_dian; RADIAN)* | id PK · factura_proveedor_id FK · evento · fecha_evento · cude_evento · concepto/descripcion_reclamo · estado |
### obligacion_pago | id PK · empresa_id FK · proveedor_id FK · factura_proveedor_id FK · cuenta_id FK · numero · fecha · fecha_vencimiento · valor_total · saldo · estado CHECK(pendiente\|parcial\|pagada\|anulada) |
### obligacion_pago_retencion *(← cpp_detalles_deducibles_retefuente)* | id PK · obligacion_pago_id FK · impuesto_id FK · base · limite · valor |

## Módulo Activos Fijos (V18)
### poliza_seguro | id PK · empresa_id FK · numero UNIQUE · aseguradora_id FK→tercero · tipo · fecha_inicio/fin · valor_asegurado |
### activo_fijo *(← acf_activos_fijos)* | id PK · empresa_id FK · producto_id FK · codigo · nombre · marca_id FK · numero_serie · modelo · bodega_id FK · centro_costo_id FK · proveedor_id FK · valor_compra/salvamento · metodo/tipo_depreciacion · valor_actual · vida_util · estado_activo |
### depreciacion *(← acf_detalles_depreciaciones; APPEND-ONLY)* | id PK · activo_fijo_id FK · fecha_aplicacion · valor_depreciacion · cuota · periodo_amortizacion |
### activo_fijo_responsable | activo_fijo_id FK · tercero_id FK |
### activo_fijo_poliza | activo_fijo_id FK · poliza_seguro_id FK |

## Módulo Contratación (V19)
### modalidad_contrato *(← ctr_modalidades)* | id PK · empresa_id FK · codigo · nombre · descripcion |
### clausula_plantilla *(← ctr_plantillas_clausulas)* | id PK · empresa_id FK · tipo_clausula · numero · nombre · texto |
### contrato *(← ctr_contratos)* | id PK · empresa_id FK · numero · nombre · tipo_contrato · contratista_id FK · modalidad_id FK · objeto · fecha_inicio/fin · valor · estado CHECK(planeado\|adjudicado\|suscrito\|en_ejecucion\|liquidado\|anulado) |
### contrato_clausula *(← ctr_contratos_detalles)* | id PK · contrato_id FK · numero · nombre · texto |
### contrato_poliza | contrato_id FK · poliza_seguro_id FK |

---

## Módulo Talento Humano (V20)  *(empleado = tercero rol empleado)*
nivel_estudio (catálogo) · empleado_estudio · empleado_familiar · empleado_historia_laboral ·
evaluacion_programa · evaluacion_desempeno. (FK satélites → tercero empleado).

## Módulo Nómina (V21, núcleo)
cargo · grupo_nomina · vinculacion (← nom_vinculaciones, rica) · concepto_nomina (formula, cuentas, rubro) ·
novedad_nomina (incl. embargos) · liquidacion_nomina (estados abierto→liquidado→revisado→contabilizado→cerrado) ·
liquidacion_nomina_detalle (APPEND-ONLY, base/%/valor empleado/patrono/entidad) · aporte_pila (salud/pension/arl/ccf/sena/icbf).

## Módulo Académico (V22)
institucion_snies · programa_academico · asignatura · asignatura_programa · grupo_academico ·
carga_academica (← vinculacion docente) · carga_academica_detalle. (carga docente → insumo nómina catedráticos).

## Módulo Jurídico (V23)
clasificacion_falta · falta · proceso_disciplinario · proceso_falta · proceso_descargo · proceso_notificacion.

---

## Migraciones actuales (modelado COMPLETO)
- `V1`–`V13`: Administración, Común, Contabilidad, Inventario, Compras, Facturación, Cartera, Caja.
- `V14` costos · `V15` presupuesto · `V16` tesorería · `V17` cuentas por pagar · `V18` activos fijos · `V19` contratación.
- `V20` talento humano · `V21` nómina · `V22` académico · `V23` jurídico.
- **18 módulos modelados.** ⛔ Salud (his/cir) EXCLUIDO por decisión del usuario.
- Opcional pendiente: soporte transversal (motor de documentos universal, adjuntos, importador).

## Pendiente (tras la BD completa)
- Semillas (plan de cuentas, impuestos, tipos de documento, geografía DANE).
- Ejecutar Flyway (docker-compose) y validar.
- Sprints + vertical slices reactivos por HU.
