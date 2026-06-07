-- ============================================================================
-- V3 — Datos semilla del núcleo (SOLO lo independiente de validaciones)
-- ERP MVP · PostgreSQL · Fase 11
--
-- Aquí se siembra ÚNICAMENTE lo que NO depende de las preguntas bloqueantes:
--   - Permisos base del RBAC (catálogo global, no dependen del cliente).
--   - Unidades de medida estándar.
--
-- NO se siembran todavía (dependen de validación sobre la BD viva):
--   - Plan de cuentas        -> Q3/V8 (Fase 10)
--   - Catálogo de impuestos  -> Q3/V8 (tarifas reales por vigencia)
--   - Tipos de documento     -> Q4/V6 (códigos/prefijos/reinicio reales)
--   - Empresa/sedes           -> alta operativa, no semilla técnica
-- Esos irán en migraciones V4+ una vez cerrada la bitácora de la Fase 10.
-- ============================================================================

-- ----------------------------------------------------------------------------
-- Permisos base (catálogo global, idempotente ante reejecución no aplica:
-- Flyway versiona; esta migración corre una sola vez)
-- ----------------------------------------------------------------------------
INSERT INTO permiso (codigo, descripcion) VALUES
    ('administracion.empresa.gestionar',      'Crear/editar empresas'),
    ('administracion.sede.gestionar',         'Crear/editar sedes'),
    ('administracion.usuario.gestionar',      'Crear/editar usuarios y roles'),
    ('administracion.vigencia.abrir',         'Abrir una vigencia'),
    ('administracion.vigencia.cerrar',        'Cerrar una vigencia'),
    ('comun.tercero.gestionar',               'Crear/editar terceros'),
    ('comun.producto.gestionar',              'Crear/editar productos'),
    ('comun.centro_costo.gestionar',          'Crear/editar centros de costo'),
    ('compras.orden.crear',                   'Crear órdenes de compra'),
    ('compras.orden.aprobar',                 'Aprobar órdenes de compra'),
    ('inventario.movimiento.registrar',       'Registrar movimientos de inventario'),
    ('facturacion.factura.emitir',            'Emitir facturas de venta'),
    ('facturacion.factura.anular',            'Anular facturas de venta'),
    ('caja.recibo.registrar',                 'Registrar recibos de caja'),
    ('caja.arqueo.realizar',                  'Realizar arqueo de caja'),
    ('cartera.acuerdo.gestionar',             'Crear/editar acuerdos de pago'),
    ('contabilidad.comprobante.confirmar',    'Confirmar comprobantes contables'),
    ('contabilidad.periodo.cerrar',           'Cerrar periodos contables');

-- ----------------------------------------------------------------------------
-- Unidades de medida estándar (catálogo global)
-- ----------------------------------------------------------------------------
INSERT INTO unidad_medida (codigo, nombre) VALUES
    ('UND', 'Unidad'),
    ('KG',  'Kilogramo'),
    ('GR',  'Gramo'),
    ('LT',  'Litro'),
    ('ML',  'Mililitro'),
    ('MT',  'Metro'),
    ('CM',  'Centímetro'),
    ('CAJ', 'Caja'),
    ('PAQ', 'Paquete'),
    ('HOR', 'Hora'),
    ('SRV', 'Servicio');
