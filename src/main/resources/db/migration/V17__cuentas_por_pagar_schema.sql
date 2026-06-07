-- ============================================================================
-- V17 — Cuentas por Pagar — mapeado de cpp_facturas_dian / cpp_eventos_facturas_dian /
--      cpp_detalles_documentos_soportes / cpp_detalles_deducibles_retefuente
-- Nyxora · PostgreSQL · esquema public
--
-- factura_proveedor = recepción de FE de proveedor (DIAN/RADIAN). obligacion_pago = la cuenta
-- por pagar limpia (el real la tenía en el documento universal). El pago lo hace
-- comprobante_egreso (V16) vía origen_modulo/origen_id.
-- Depende de: V1, V2 (impuesto), V5 (tercero), V8 (cuenta).
-- ============================================================================

-- factura_proveedor (← cpp_facturas_dian)
CREATE TABLE factura_proveedor (
    id                BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    empresa_id        BIGINT        NOT NULL,
    proveedor_id      BIGINT        NOT NULL,        -- ← com_tercero_emisor_id
    receptor_id       BIGINT,                        -- ← com_tercero_receptor_id
    numero_documento  VARCHAR(60),                   -- ← documento
    cufe              VARCHAR(191),
    fecha_recepcion   DATE,
    valor_factura     NUMERIC(19,4) NOT NULL DEFAULT 0,
    email_remitente   VARCHAR(191),
    xml_factura       TEXT,
    pdf_url           TEXT,
    estado            VARCHAR(20)   NOT NULL DEFAULT 'recibida',
    activo            BOOLEAN       NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, updated_at TIMESTAMP, deleted_at TIMESTAMP,
    usuario_creacion BIGINT, usuario_modificacion BIGINT,
    CONSTRAINT fk_facprov_empresa   FOREIGN KEY (empresa_id)   REFERENCES empresa (id),
    CONSTRAINT fk_facprov_proveedor FOREIGN KEY (proveedor_id) REFERENCES tercero (id),
    CONSTRAINT fk_facprov_receptor  FOREIGN KEY (receptor_id)  REFERENCES tercero (id)
);
CREATE INDEX ix_facprov_empresa ON factura_proveedor (empresa_id);

-- factura_proveedor_evento (← cpp_eventos_facturas_dian) — eventos RADIAN
CREATE TABLE factura_proveedor_evento (
    id                   BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    factura_proveedor_id BIGINT      NOT NULL,
    evento               VARCHAR(50),                -- ← evento_id
    fecha_evento         DATE,
    cude_evento          VARCHAR(255),
    concepto_reclamo     VARCHAR(255),
    descripcion_reclamo  TEXT,
    estado               VARCHAR(50),
    error_evento         VARCHAR(1000),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, deleted_at TIMESTAMP,
    CONSTRAINT fk_facprov_evento_factura FOREIGN KEY (factura_proveedor_id) REFERENCES factura_proveedor (id)
);
CREATE INDEX ix_facprov_evento_factura ON factura_proveedor_evento (factura_proveedor_id);

-- obligacion_pago (la cuenta por pagar)
CREATE TABLE obligacion_pago (
    id                   BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    empresa_id           BIGINT        NOT NULL,
    proveedor_id         BIGINT        NOT NULL,
    factura_proveedor_id BIGINT,
    cuenta_id            BIGINT,                      -- cuenta contable por pagar
    numero               VARCHAR(40),
    fecha                DATE          NOT NULL,
    fecha_vencimiento    DATE,
    valor_total          NUMERIC(19,4) NOT NULL DEFAULT 0,
    saldo                NUMERIC(19,4) NOT NULL DEFAULT 0,
    estado               VARCHAR(15)   NOT NULL DEFAULT 'pendiente',
    activo               BOOLEAN       NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, updated_at TIMESTAMP, deleted_at TIMESTAMP,
    usuario_creacion BIGINT, usuario_modificacion BIGINT,
    CONSTRAINT fk_oblig_empresa   FOREIGN KEY (empresa_id)           REFERENCES empresa (id),
    CONSTRAINT fk_oblig_proveedor FOREIGN KEY (proveedor_id)         REFERENCES tercero (id),
    CONSTRAINT fk_oblig_factura   FOREIGN KEY (factura_proveedor_id) REFERENCES factura_proveedor (id),
    CONSTRAINT fk_oblig_cuenta    FOREIGN KEY (cuenta_id)            REFERENCES cuenta (id),
    CONSTRAINT ck_oblig_estado    CHECK (estado IN ('pendiente','parcial','pagada','anulada'))
);
CREATE INDEX ix_oblig_empresa ON obligacion_pago (empresa_id);

-- obligacion_pago_retencion (← cpp_detalles_deducibles_retefuente)
CREATE TABLE obligacion_pago_retencion (
    id                BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    obligacion_pago_id BIGINT       NOT NULL,
    impuesto_id       BIGINT,                        -- retención (← nom_concepto_id/impuesto)
    base              NUMERIC(19,4),
    limite            VARCHAR(25),
    valor             NUMERIC(19,4) NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, deleted_at TIMESTAMP,
    CONSTRAINT fk_oblig_ret_oblig    FOREIGN KEY (obligacion_pago_id) REFERENCES obligacion_pago (id),
    CONSTRAINT fk_oblig_ret_impuesto FOREIGN KEY (impuesto_id)        REFERENCES impuesto (id)
);
CREATE INDEX ix_oblig_ret_oblig ON obligacion_pago_retencion (obligacion_pago_id);
