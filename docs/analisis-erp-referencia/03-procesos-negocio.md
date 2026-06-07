# Fase 3 — Procesos de negocio identificados

> Reconstrucción inferida a partir de tablas, columnas y nombres de controladores.
> Cada proceso: inicio · información · tablas afectadas · estados · aprobaciones · movimientos · integraciones · resultado.

## Patrón transversal: el "documento" como unidad de proceso

🧠 **Interpretación clave.** Casi todo proceso transaccional se materializa como un **documento**:
una fila en `com_encabezados_documentos` (tipada por `com_documento_id` + `com_concepto_id`) con
sus líneas en la tabla `*_detalles_*` del módulo. El documento avanza por una **máquina de estados**
(`estado` numérico), puede tener **workflow/aprobación** (`com_encabezados_documentos_workflow`),
**referencias** a otros documentos (`*_referencias`) y, al confirmarse, **genera interfaces**
contables y presupuestales (`*_detalles_contabilidad`, `*_detalles_presupuesto`).

```
[Borrador] → [Revisión/Workflow] → [Aprobado/Confirmado] → [Contabilizado/Presupuestado] → [Cerrado/Anulado]
                     │                        │                         │
              com_..._workflow        consecutivo asignado     interfaces a con_/pre_
```

---

## P1 — Compras y contratación (abastecimiento)

- **Inicio:** plan de adquisiciones (`cmp_planes_adquisiciones`).
- **Info:** productos (`com_productos`), proveedores (`com_productos_proveedores`, `com_terceros`),
  presupuesto disponible (`pre_saldos_*`).
- **Flujo:** estudio previo (riesgos/ponderación) → cuadro comparativo (`cmp_cuadros_comparativos_detalles`)
  → contrato (`ctr_contratos`, modalidad, cláusulas, pólizas) → orden de compra (documento) →
  recepción a inventario.
- **Estados:** planeado → en proceso → adjudicado → contratado → ejecutado.
- **Integraciones:** Presupuesto (compromiso/CDP), Inventario (entrada), CxP (causación), Contabilidad.
- **Resultado:** contrato y compromiso presupuestal; productos disponibles en bodega.

## P2 — Inventario (entradas/salidas/saldos)

- **Inicio:** requisición / orden / recepción.
- **Tablas:** `inv_bodegas`, `inv_detalles_inventarios`, `inv_lotes`, `inv_saldos_productos`,
  `inv_ubicaciones`, `inv_cierres`.
- **Movimientos:** entradas, salidas, traslados entre bodegas (de ahí `inv_bodega1/2` en el encabezado).
- **Estados:** abierto → confirmado → cerrado (cierre de inventario por periodo).
- **Integraciones:** Contabilidad (kardex valorizado), Presupuesto, Compras.
- **Resultado:** saldo de existencias actualizado y valorizado.

## P3 — Facturación de servicios/salud (ingresos)

- **Inicio:** prestación de servicio / contrato con cliente o EPS (`fac_contratos*`).
- **Info:** manual tarifario (`fac_manuales_tarifarios`), tarifas (`fac_tarifas`), copagos/cuotas
  (`fac_contratos_copagos_cuotas`), resolución DIAN (`fac_resoluciones_facturacion`).
- **Flujo:** generación de factura → firma digital (`fac_firmas_digitales`) → envío DIAN
  (`fac_facturas_electronicas`) → manejo de errores (`fac_factura_electronica_errores`,
  `fac_facturas_errores_dian`) → recaudo (`fac_recaudos`).
- **Estados:** borrador → emitida → aceptada/rechazada DIAN → recaudada/anulada.
- **Integraciones:** Cartera (saldo por cobrar), Caja/Tesorería (recaudo), Contabilidad, Presupuesto (ingreso).
- **Resultado:** factura electrónica válida + cuenta por cobrar.

## P4 — Cartera (recuperación)

- **Inicio:** factura emitida no recaudada.
- **Tablas:** `cpc_detalles_cartera`, `cpc_referencias_cartera`, `cpc_detalles_acuerdos_pago`,
  `com_edades_cuentas`.
- **Flujo:** edades de cartera → gestión de cobro → acuerdos de pago → recaudo/castigo.
- **Integraciones:** Facturación, Caja/Tesorería, Contabilidad (provisión/difícil cobro).

## P5 — Cuentas por pagar (causación de proveedores)

- **Inicio:** recepción de factura electrónica de proveedor vía RADIAN (`cpp_facturas_dian`,
  `cpp_eventos_facturas_dian`, `prv_correos_radian`).
- **Tablas:** `cpp_detalles_documentos_soportes`, `cpp_detalles_deducibles_retefuente`, interfaces.
- **Flujo:** recepción → validación eventos DIAN → causación → programación de pago.
- **Integraciones:** Contabilidad (retenciones), Presupuesto (obligación), Tesorería (pago).

## P6 — Tesorería (egresos y bancos)

- **Inicio:** obligación por pagar / programación PAC.
- **Tablas:** `tes_detalles_comprobantes_egresos`, `tes_detalles_giros`, `tes_chequeras*`,
  `tes_cuentas_bancarias`, `tes_conciliacion_bancaria`, `tes_extractos_bancarios(_detalles)`,
  `tes_detalles_cuentas_pagar`.
- **Flujo:** comprobante de egreso → giro/cheque/transferencia → conciliación contra extracto.
- **Estados:** programado → girado → conciliado.
- **Integraciones:** CxP, Presupuesto (pago), Contabilidad.

## P7 — Caja (recaudo en punto)

- **Tablas:** `caj_detalles_recaudos`, `caj_detalles_recibos_caja`, `caj_detalles_cajas`, interfaces.
- **Flujo:** apertura de caja → recibos de caja → arqueo/cierre → traslado a tesorería.
- **Integraciones:** Facturación/Cartera, Tesorería, Contabilidad.

## P8 — Nómina (liquidación de personal)

- **Inicio:** apertura de proceso de nómina del periodo (`nom_procesos`).
- **Info:** vinculaciones (`nom_vinculaciones`), cargos/conceptos (`nom_cargos_conceptos`,
  `nom_conceptos_bases`), novedades (`nom_novedades*`), embargos/descuentos, salarios históricos.
- **Flujo:** captura de novedades → cálculo (`nom_detalles_liquidaciones`, con `_temp`/`_errores`)
  → revisión → liquidación definitiva → PILA (`nom_pila_*`) → pago.
- **Estados:** abierto → liquidado → revisado → contabilizado/pagado → cerrado.
- **Integraciones:** Académico (carga docente → catedráticos), Presupuesto, Contabilidad, Tesorería.
- **Resultado:** desprendibles, archivo PILA, asiento contable y compromiso presupuestal.

## P9 — Gestión académica (universidad)

- **Tablas:** `aca_programas_academicos`, `aca_asignaturas`, `aca_grupos_academicos`,
  `aca_cargas_academicas(_detalles)`, `aca_tarifas_catedras`.
- **Flujo:** plan de estudios → asignación de carga docente → tarifa de cátedra → insumo de nómina.
- **Integraciones:** Nómina (catedráticos), Facturación (recibos `fac_recibos_academulsoft`).

## P10 — Activos fijos

- **Tablas:** `acf_activos_fijos`, `acf_detalles_depreciaciones`, `acf_responsables_activos_fijos`,
  `acf_polizas_seguros`, `acf_cierres`.
- **Flujo:** alta de activo → asignación a responsable → depreciación periódica → baja.
- **Integraciones:** Contabilidad (depreciación), Compras (alta), Seguros.

## P11 — Contabilidad y cierre

- **Flujo:** recepción de interfaces de todos los módulos (`*_detalles_contabilidad/_contables`)
  → asientos → saldos por ejes (`con_saldos_*`) → conciliaciones/reclasificaciones →
  estados financieros (`con_estados_financieros`) → información exógena DIAN → **cierre**
  (`con_cierres_contables`, `adm_cierres_mes`).
- **Estados de periodo:** abierto → en cierre → cerrado.

## P12 — Presupuesto (ciclo público)

- **Flujo:** plan presupuestal por vigencia (`pre_planes_presupuestales`) → CDP (disponibilidad)
  → compromiso → obligación → pago, con afectación de `pre_saldos_*` y PAC (`pre_pac_centros_costos`)
  → cierre (`pre_cierres_presupuestales`).
- **Integraciones:** Compras, Nómina, CxP, Tesorería, Contabilidad.

## P13 — Procesos transversales de soporte

- **Apertura/cierre de vigencia** (`com_apertura_vigencia*`): habilita el año fiscal en todos los módulos.
- **Numeración de documentos** (`com_documentos_consecutivos`): consecutivos por tipo/sede/vigencia.
- **Importador** (`com_importador*`): cargas masivas con validación y registro de errores.
- **Workflow/aprobaciones** (`com_encabezados_documentos_workflow`, `adm_flujos_trabajos`).
- **Auditoría** (`audits`, `audits_login`) y **notificaciones/colas** (`jobs`, `notifications`).
- **Procesos disciplinarios** (`jur_*`): faltas → descargos → notificaciones → decisión.
