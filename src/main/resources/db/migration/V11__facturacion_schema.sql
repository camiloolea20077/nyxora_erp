-- ============================================================================
-- V11 — Facturación — mapeado de fac_resoluciones_facturacion / fac_detalles_facturacion
--      / fac_facturas_electronicas
-- Nyxora · PostgreSQL · esquema public
--
-- La factura se modela limpia (el real la guardaba en el documento universal). La metadata
-- DIAN va en factura_dian. Se POSPONE la facturación de salud (contratos EPS, copagos,
-- manuales tarifarios, capitación). Depende de: V1, V2, V4, V5, V6, V7, V9.
-- ============================================================================

-- resolucion_dian (← fac_resoluciones_facturacion)
CREATE TABLE resolucion_dian (
    id                   BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    empresa_id           BIGINT      NOT NULL,
    numero_resolucion    VARCHAR(20) NOT NULL,
    prefijo              VARCHAR(5),
    factura_inicial      BIGINT,
    factura_final        BIGINT,
    fecha_inicial        DATE,
    fecha_final          DATE,
    clave_tecnica        VARCHAR(150),
    descripcion          VARCHAR(500),
    consecutivo_actual   BIGINT      NOT NULL DEFAULT 0,
    activo               BOOLEAN     NOT NULL DEFAULT TRUE,
    created_at           TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at           TIMESTAMP, deleted_at TIMESTAMP,
    usuario_creacion     BIGINT, usuario_modificacion BIGINT,
    CONSTRAINT fk_resolucion_empresa FOREIGN KEY (empresa_id) REFERENCES empresa (id),
    CONSTRAINT uq_resolucion_numero  UNIQUE (empresa_id, numero_resolucion)
);

-- factura (encabezado)
CREATE TABLE factura (
    id                   BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    empresa_id           BIGINT        NOT NULL,
    sede_id              BIGINT,
    vigencia_id          BIGINT,
    tipo_documento_id    BIGINT,
    resolucion_dian_id   BIGINT,
    numero               VARCHAR(40),
    cliente_id           BIGINT        NOT NULL,        -- tercero rol cliente
    bodega_id            BIGINT,                        -- bodega que descuenta
    centro_costo_id      BIGINT,
    condicion_pago_id    BIGINT,
    fecha                DATE          NOT NULL,
    fecha_vencimiento    DATE,
    observaciones        VARCHAR(500),
    estado               VARCHAR(15)   NOT NULL DEFAULT 'borrador',
    subtotal             NUMERIC(19,4) NOT NULL DEFAULT 0,
    descuento            NUMERIC(19,4) NOT NULL DEFAULT 0,
    impuestos            NUMERIC(19,4) NOT NULL DEFAULT 0,
    total                NUMERIC(19,4) NOT NULL DEFAULT 0,
    activo               BOOLEAN       NOT NULL DEFAULT TRUE,
    created_at           TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at           TIMESTAMP, deleted_at TIMESTAMP,
    usuario_creacion     BIGINT, usuario_modificacion BIGINT,
    CONSTRAINT fk_factura_empresa        FOREIGN KEY (empresa_id)         REFERENCES empresa (id),
    CONSTRAINT fk_factura_sede           FOREIGN KEY (sede_id)            REFERENCES sede (id),
    CONSTRAINT fk_factura_vigencia       FOREIGN KEY (vigencia_id)        REFERENCES vigencia (id),
    CONSTRAINT fk_factura_tipo_doc       FOREIGN KEY (tipo_documento_id)  REFERENCES tipo_documento (id),
    CONSTRAINT fk_factura_resolucion     FOREIGN KEY (resolucion_dian_id) REFERENCES resolucion_dian (id),
    CONSTRAINT fk_factura_cliente        FOREIGN KEY (cliente_id)         REFERENCES tercero (id),
    CONSTRAINT fk_factura_bodega         FOREIGN KEY (bodega_id)          REFERENCES bodega (id),
    CONSTRAINT fk_factura_centro_costo   FOREIGN KEY (centro_costo_id)    REFERENCES centro_costo (id),
    CONSTRAINT fk_factura_condicion_pago FOREIGN KEY (condicion_pago_id)  REFERENCES condicion_pago (id),
    CONSTRAINT ck_factura_estado CHECK (estado IN ('borrador', 'emitida', 'anulada'))
);
CREATE INDEX ix_factura_empresa ON factura (empresa_id);
CREATE INDEX ix_factura_cliente ON factura (cliente_id);

-- factura_linea (← fac_detalles_facturacion)
CREATE TABLE factura_linea (
    id                   BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    factura_id           BIGINT        NOT NULL,
    producto_id          BIGINT        NOT NULL,
    producto_variante_id BIGINT,
    descripcion          VARCHAR(500),
    cantidad             NUMERIC(19,4) NOT NULL,
    unidad_medida_id     BIGINT,
    valor_unitario       NUMERIC(19,4) NOT NULL DEFAULT 0,
    descuento_porcentaje NUMERIC(7,4),
    descuento_valor      NUMERIC(19,4),
    subtotal             NUMERIC(19,4) NOT NULL DEFAULT 0,
    impuesto_id          BIGINT,
    porcentaje_impuesto  NUMERIC(7,4),
    valor_impuesto       NUMERIC(19,4),
    discrimina_iva       BOOLEAN       NOT NULL DEFAULT FALSE,
    total                NUMERIC(19,4) NOT NULL DEFAULT 0,
    bodega_id            BIGINT,
    lote_id              BIGINT,
    centro_costo_id      BIGINT,
    created_at           TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at           TIMESTAMP, deleted_at TIMESTAMP,
    CONSTRAINT fk_fl_factura      FOREIGN KEY (factura_id)           REFERENCES factura (id),
    CONSTRAINT fk_fl_producto     FOREIGN KEY (producto_id)          REFERENCES producto (id),
    CONSTRAINT fk_fl_variante     FOREIGN KEY (producto_variante_id) REFERENCES producto_variante (id),
    CONSTRAINT fk_fl_unidad       FOREIGN KEY (unidad_medida_id)     REFERENCES unidad_medida (id),
    CONSTRAINT fk_fl_impuesto     FOREIGN KEY (impuesto_id)          REFERENCES impuesto (id),
    CONSTRAINT fk_fl_bodega       FOREIGN KEY (bodega_id)            REFERENCES bodega (id),
    CONSTRAINT fk_fl_lote         FOREIGN KEY (lote_id)              REFERENCES lote (id),
    CONSTRAINT fk_fl_centro_costo FOREIGN KEY (centro_costo_id)      REFERENCES centro_costo (id)
);
CREATE INDEX ix_factura_linea_factura ON factura_linea (factura_id);

-- factura_dian (← fac_facturas_electronicas) — metadata de la FE
CREATE TABLE factura_dian (
    id               BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    factura_id       BIGINT      NOT NULL,
    cufe             VARCHAR(150),                 -- ← token/CUFE
    estado_dian      VARCHAR(20),                  -- enviada | aceptada | rechazada
    fecha_acuse      DATE,
    comentario_acuse TEXT,
    created_at       TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at       TIMESTAMP, deleted_at TIMESTAMP,
    CONSTRAINT fk_factura_dian_factura FOREIGN KEY (factura_id) REFERENCES factura (id),
    CONSTRAINT uq_factura_dian_factura UNIQUE (factura_id)
);
