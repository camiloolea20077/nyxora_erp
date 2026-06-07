-- ============================================================================
-- V10 — Compras (orden de compra + recepción)
-- Nyxora · PostgreSQL · esquema public
--
-- NOTA: el ERP de referencia NO tiene una 'orden_compra' limpia (las compras viven en
-- el documento universal com_encabezados_documentos + planes de adquisición cmp_*).
-- Aquí se modela limpia (diseño Fase 9), con los campos del detalle real (cantidad,
-- unitario, descuento, impuesto, subtotal, total) y trazabilidad a inventario.
-- Depende de: V1, V2 (tipo_documento, unidad_medida, impuesto), V4 (condicion_pago),
--             V5 (tercero), V6 (producto, producto_variante), V7 (centro_costo), V9 (bodega, lote, ubicacion).
-- ============================================================================

-- ----------------------------------------------------------------------------
-- orden_compra (encabezado)
-- ----------------------------------------------------------------------------
CREATE TABLE orden_compra (
    id                   BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    empresa_id           BIGINT        NOT NULL,
    sede_id              BIGINT,
    vigencia_id          BIGINT,
    tipo_documento_id    BIGINT,
    numero               VARCHAR(40),
    proveedor_id         BIGINT        NOT NULL,        -- tercero rol proveedor
    bodega_id            BIGINT,                        -- bodega destino
    centro_costo_id      BIGINT,
    condicion_pago_id    BIGINT,
    fecha                DATE          NOT NULL,
    fecha_entrega        DATE,
    observaciones        VARCHAR(500),
    estado               VARCHAR(20)   NOT NULL DEFAULT 'borrador',
    subtotal             NUMERIC(19,4) NOT NULL DEFAULT 0,
    descuento            NUMERIC(19,4) NOT NULL DEFAULT 0,
    impuestos            NUMERIC(19,4) NOT NULL DEFAULT 0,
    total                NUMERIC(19,4) NOT NULL DEFAULT 0,
    activo               BOOLEAN       NOT NULL DEFAULT TRUE,
    created_at           TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at           TIMESTAMP, deleted_at TIMESTAMP,
    usuario_creacion     BIGINT, usuario_modificacion BIGINT,
    CONSTRAINT fk_oc_empresa       FOREIGN KEY (empresa_id)        REFERENCES empresa (id),
    CONSTRAINT fk_oc_sede          FOREIGN KEY (sede_id)           REFERENCES sede (id),
    CONSTRAINT fk_oc_vigencia      FOREIGN KEY (vigencia_id)       REFERENCES vigencia (id),
    CONSTRAINT fk_oc_tipo_doc      FOREIGN KEY (tipo_documento_id) REFERENCES tipo_documento (id),
    CONSTRAINT fk_oc_proveedor     FOREIGN KEY (proveedor_id)      REFERENCES tercero (id),
    CONSTRAINT fk_oc_bodega        FOREIGN KEY (bodega_id)         REFERENCES bodega (id),
    CONSTRAINT fk_oc_centro_costo  FOREIGN KEY (centro_costo_id)   REFERENCES centro_costo (id),
    CONSTRAINT fk_oc_condicion_pago FOREIGN KEY (condicion_pago_id) REFERENCES condicion_pago (id),
    CONSTRAINT ck_oc_estado CHECK (estado IN ('borrador','aprobada','recibida_parcial','recibida_total','cerrada','anulada'))
);
CREATE INDEX ix_oc_empresa   ON orden_compra (empresa_id);
CREATE INDEX ix_oc_proveedor ON orden_compra (proveedor_id);

-- ----------------------------------------------------------------------------
-- orden_compra_linea (detalle)
-- ----------------------------------------------------------------------------
CREATE TABLE orden_compra_linea (
    id                   BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    orden_compra_id      BIGINT        NOT NULL,
    producto_id          BIGINT        NOT NULL,
    producto_variante_id BIGINT,
    descripcion          VARCHAR(500),
    cantidad             NUMERIC(19,4) NOT NULL,
    unidad_medida_id     BIGINT,
    valor_unitario       NUMERIC(19,4) NOT NULL DEFAULT 0,
    descuento_porcentaje NUMERIC(7,4),
    descuento_valor      NUMERIC(19,4),
    impuesto_id          BIGINT,
    impuesto_porcentaje  NUMERIC(7,4),
    impuesto_valor       NUMERIC(19,4),
    subtotal             NUMERIC(19,4) NOT NULL DEFAULT 0,
    total                NUMERIC(19,4) NOT NULL DEFAULT 0,
    cantidad_recibida    NUMERIC(19,4) NOT NULL DEFAULT 0,
    cantidad_pendiente   NUMERIC(19,4),
    centro_costo_id      BIGINT,
    created_at           TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at           TIMESTAMP, deleted_at TIMESTAMP,
    CONSTRAINT fk_ocl_orden        FOREIGN KEY (orden_compra_id)      REFERENCES orden_compra (id),
    CONSTRAINT fk_ocl_producto     FOREIGN KEY (producto_id)          REFERENCES producto (id),
    CONSTRAINT fk_ocl_variante     FOREIGN KEY (producto_variante_id) REFERENCES producto_variante (id),
    CONSTRAINT fk_ocl_unidad       FOREIGN KEY (unidad_medida_id)     REFERENCES unidad_medida (id),
    CONSTRAINT fk_ocl_impuesto     FOREIGN KEY (impuesto_id)          REFERENCES impuesto (id),
    CONSTRAINT fk_ocl_centro_costo FOREIGN KEY (centro_costo_id)      REFERENCES centro_costo (id)
);
CREATE INDEX ix_ocl_orden ON orden_compra_linea (orden_compra_id);

-- ----------------------------------------------------------------------------
-- recepcion (entrada de mercancía contra la orden → alimenta inventario)
-- ----------------------------------------------------------------------------
CREATE TABLE recepcion (
    id                   BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    empresa_id           BIGINT      NOT NULL,
    orden_compra_id      BIGINT      NOT NULL,
    bodega_id            BIGINT      NOT NULL,
    tipo_documento_id    BIGINT,
    numero               VARCHAR(40),
    fecha                DATE        NOT NULL,
    estado               VARCHAR(15) NOT NULL DEFAULT 'borrador',
    observaciones        VARCHAR(500),
    activo               BOOLEAN     NOT NULL DEFAULT TRUE,
    created_at           TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at           TIMESTAMP, deleted_at TIMESTAMP,
    usuario_creacion     BIGINT, usuario_modificacion BIGINT,
    CONSTRAINT fk_rec_empresa  FOREIGN KEY (empresa_id)        REFERENCES empresa (id),
    CONSTRAINT fk_rec_orden    FOREIGN KEY (orden_compra_id)   REFERENCES orden_compra (id),
    CONSTRAINT fk_rec_bodega   FOREIGN KEY (bodega_id)         REFERENCES bodega (id),
    CONSTRAINT fk_rec_tipo_doc FOREIGN KEY (tipo_documento_id) REFERENCES tipo_documento (id),
    CONSTRAINT ck_rec_estado CHECK (estado IN ('borrador','confirmada','anulada'))
);
CREATE INDEX ix_rec_empresa ON recepcion (empresa_id);
CREATE INDEX ix_rec_orden   ON recepcion (orden_compra_id);

-- ----------------------------------------------------------------------------
-- recepcion_linea
-- ----------------------------------------------------------------------------
CREATE TABLE recepcion_linea (
    id                    BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    recepcion_id          BIGINT        NOT NULL,
    orden_compra_linea_id BIGINT        NOT NULL,
    producto_id           BIGINT        NOT NULL,
    producto_variante_id  BIGINT,
    lote_id               BIGINT,
    ubicacion_id          BIGINT,
    cantidad_recibida     NUMERIC(19,4) NOT NULL,
    costo_unitario        NUMERIC(19,4) NOT NULL DEFAULT 0,
    created_at            TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at            TIMESTAMP,
    CONSTRAINT fk_recl_recepcion FOREIGN KEY (recepcion_id)          REFERENCES recepcion (id),
    CONSTRAINT fk_recl_oc_linea  FOREIGN KEY (orden_compra_linea_id) REFERENCES orden_compra_linea (id),
    CONSTRAINT fk_recl_producto  FOREIGN KEY (producto_id)           REFERENCES producto (id),
    CONSTRAINT fk_recl_variante  FOREIGN KEY (producto_variante_id)  REFERENCES producto_variante (id),
    CONSTRAINT fk_recl_lote      FOREIGN KEY (lote_id)               REFERENCES lote (id),
    CONSTRAINT fk_recl_ubicacion FOREIGN KEY (ubicacion_id)          REFERENCES ubicacion (id)
);
CREATE INDEX ix_recl_recepcion ON recepcion_linea (recepcion_id);
