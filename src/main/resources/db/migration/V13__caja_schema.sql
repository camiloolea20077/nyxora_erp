-- ============================================================================
-- V13 — Caja (recaudo en punto) — mapeado de caj_detalles_recibos_caja / caj_detalles_recaudos
-- Nyxora · PostgreSQL · esquema public
--
-- recibo_caja (encabezado) + recibo_caja_pago (medios de pago, ← caj_detalles_recaudos)
-- + recibo_caja_linea (aplicación a cuentas por cobrar, ← caj_detalles_recibos_caja) + arqueo.
-- Depende de: V1 (empresa, sede, usuario), V2 (tipo_documento), V4 (forma_pago, banco),
--             V5 (tercero), V12 (cuenta_por_cobrar).
-- ============================================================================

-- caja (punto de recaudo)
CREATE TABLE caja (
    id                   BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    empresa_id           BIGINT        NOT NULL,
    sede_id              BIGINT,
    usuario_id           BIGINT,
    codigo               VARCHAR(20)   NOT NULL,
    nombre               VARCHAR(150),
    estado               VARCHAR(15)   NOT NULL DEFAULT 'cerrada',
    saldo_inicial        NUMERIC(19,4) NOT NULL DEFAULT 0,
    fecha_apertura       TIMESTAMP,
    fecha_cierre         TIMESTAMP,
    activo               BOOLEAN       NOT NULL DEFAULT TRUE,
    created_at           TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at           TIMESTAMP, deleted_at TIMESTAMP,
    usuario_creacion     BIGINT, usuario_modificacion BIGINT,
    CONSTRAINT fk_caja_empresa FOREIGN KEY (empresa_id) REFERENCES empresa (id),
    CONSTRAINT fk_caja_sede    FOREIGN KEY (sede_id)    REFERENCES sede (id),
    CONSTRAINT fk_caja_usuario FOREIGN KEY (usuario_id) REFERENCES usuario (id),
    CONSTRAINT uq_caja_codigo  UNIQUE (empresa_id, codigo),
    CONSTRAINT ck_caja_estado  CHECK (estado IN ('abierta', 'cerrada'))
);
CREATE INDEX ix_caja_empresa ON caja (empresa_id);

-- recibo_caja (encabezado del recaudo)
CREATE TABLE recibo_caja (
    id                   BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    empresa_id           BIGINT        NOT NULL,
    caja_id              BIGINT        NOT NULL,
    tipo_documento_id    BIGINT,
    numero               VARCHAR(40),
    cliente_id           BIGINT,                        -- tercero que paga
    fecha                DATE          NOT NULL,
    valor                NUMERIC(19,4) NOT NULL DEFAULT 0,
    estado               VARCHAR(15)   NOT NULL DEFAULT 'registrado',
    observaciones        VARCHAR(500),
    activo               BOOLEAN       NOT NULL DEFAULT TRUE,
    created_at           TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at           TIMESTAMP, deleted_at TIMESTAMP,
    usuario_creacion     BIGINT, usuario_modificacion BIGINT,
    CONSTRAINT fk_recibo_empresa  FOREIGN KEY (empresa_id)        REFERENCES empresa (id),
    CONSTRAINT fk_recibo_caja     FOREIGN KEY (caja_id)           REFERENCES caja (id),
    CONSTRAINT fk_recibo_tipo_doc FOREIGN KEY (tipo_documento_id) REFERENCES tipo_documento (id),
    CONSTRAINT fk_recibo_cliente  FOREIGN KEY (cliente_id)        REFERENCES tercero (id),
    CONSTRAINT ck_recibo_estado   CHECK (estado IN ('registrado', 'anulado'))
);
CREATE INDEX ix_recibo_empresa ON recibo_caja (empresa_id);
CREATE INDEX ix_recibo_caja    ON recibo_caja (caja_id);

-- recibo_caja_pago (medios de pago del recibo, ← caj_detalles_recaudos)
CREATE TABLE recibo_caja_pago (
    id              BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    recibo_caja_id  BIGINT        NOT NULL,
    forma_pago_id   BIGINT,                            -- ← forma_pago_id
    valor           NUMERIC(19,4) NOT NULL,
    banco_id        BIGINT,                            -- ← banco
    numero_cheque   VARCHAR(50),
    numero_tarjeta  VARCHAR(50),
    cuenta_bancaria VARCHAR(50),
    created_at      TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at      TIMESTAMP,
    CONSTRAINT fk_recibo_pago_recibo    FOREIGN KEY (recibo_caja_id) REFERENCES recibo_caja (id),
    CONSTRAINT fk_recibo_pago_formapago FOREIGN KEY (forma_pago_id)  REFERENCES forma_pago (id),
    CONSTRAINT fk_recibo_pago_banco     FOREIGN KEY (banco_id)       REFERENCES banco (id)
);
CREATE INDEX ix_recibo_pago_recibo ON recibo_caja_pago (recibo_caja_id);

-- recibo_caja_linea (aplicación a cuentas por cobrar, ← caj_detalles_recibos_caja)
CREATE TABLE recibo_caja_linea (
    id                   BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    recibo_caja_id       BIGINT        NOT NULL,
    cuenta_por_cobrar_id BIGINT        NOT NULL,
    valor_aplicado       NUMERIC(19,4) NOT NULL,
    created_at           TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at           TIMESTAMP,
    CONSTRAINT fk_recibo_linea_recibo FOREIGN KEY (recibo_caja_id)       REFERENCES recibo_caja (id),
    CONSTRAINT fk_recibo_linea_cxc    FOREIGN KEY (cuenta_por_cobrar_id) REFERENCES cuenta_por_cobrar (id)
);
CREATE INDEX ix_recibo_linea_recibo ON recibo_caja_linea (recibo_caja_id);

-- arqueo (cuadre de caja)
CREATE TABLE arqueo (
    id               BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    empresa_id       BIGINT        NOT NULL,
    caja_id          BIGINT        NOT NULL,
    fecha            TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    valor_declarado  NUMERIC(19,4) NOT NULL DEFAULT 0,
    valor_sistema    NUMERIC(19,4) NOT NULL DEFAULT 0,
    diferencia       NUMERIC(19,4) NOT NULL DEFAULT 0,
    observaciones    VARCHAR(500),
    usuario_creacion BIGINT,
    created_at       TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_arqueo_empresa FOREIGN KEY (empresa_id) REFERENCES empresa (id),
    CONSTRAINT fk_arqueo_caja    FOREIGN KEY (caja_id)    REFERENCES caja (id)
);
CREATE INDEX ix_arqueo_caja ON arqueo (caja_id);
