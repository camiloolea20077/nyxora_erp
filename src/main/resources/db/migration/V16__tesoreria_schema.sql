-- ============================================================================
-- V16 — Tesorería — mapeado de tes_cuentas_bancarias / tes_chequeras /
--      tes_extractos_bancarios / tes_conciliacion_bancaria (+ egresos)
-- Nyxora · PostgreSQL · esquema public
--
-- 'cuenta_bancaria' aquí es la cuenta PROPIA de la empresa (distinta de tercero_cuenta_bancaria).
-- Depende de: V1, V2 (tipo_documento), V4 (banco, tipo_cuenta_bancaria, forma_pago), V5 (tercero), V8 (cuenta).
-- ============================================================================

-- cuenta_bancaria (propia de la empresa, ← tes_cuentas_bancarias)
CREATE TABLE cuenta_bancaria (
    id                      BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    empresa_id              BIGINT      NOT NULL,
    banco_id                BIGINT      NOT NULL,        -- ← com_tercero_banco_id
    tipo_cuenta_bancaria_id BIGINT,                      -- ← tipo_cuenta_id
    numero_cuenta           VARCHAR(25) NOT NULL,
    cuenta_contable_id      BIGINT,                      -- ← con_plan_contable_id
    maneja_sobregiro        BOOLEAN     NOT NULL DEFAULT FALSE,
    acepta_transferencias   BOOLEAN     NOT NULL DEFAULT FALSE,
    fecha_expiracion        DATE,
    activo                  BOOLEAN     NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, updated_at TIMESTAMP, deleted_at TIMESTAMP,
    usuario_creacion BIGINT, usuario_modificacion BIGINT,
    CONSTRAINT fk_ctabanco_empresa FOREIGN KEY (empresa_id)              REFERENCES empresa (id),
    CONSTRAINT fk_ctabanco_banco   FOREIGN KEY (banco_id)                REFERENCES banco (id),
    CONSTRAINT fk_ctabanco_tipo    FOREIGN KEY (tipo_cuenta_bancaria_id) REFERENCES tipo_cuenta_bancaria (id),
    CONSTRAINT fk_ctabanco_cuenta  FOREIGN KEY (cuenta_contable_id)      REFERENCES cuenta (id),
    CONSTRAINT uq_ctabanco_numero  UNIQUE (empresa_id, banco_id, numero_cuenta)
);
CREATE INDEX ix_ctabanco_empresa ON cuenta_bancaria (empresa_id);

-- chequera (← tes_chequeras)
CREATE TABLE chequera (
    id                  BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    empresa_id          BIGINT  NOT NULL,
    cuenta_bancaria_id  BIGINT  NOT NULL,
    fecha_expedicion    DATE,
    numero_inicial      BIGINT,
    numero_final        BIGINT,
    consecutivo_actual  BIGINT  NOT NULL DEFAULT 0,
    activo              BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, updated_at TIMESTAMP, deleted_at TIMESTAMP,
    CONSTRAINT fk_chequera_empresa FOREIGN KEY (empresa_id)         REFERENCES empresa (id),
    CONSTRAINT fk_chequera_cuenta  FOREIGN KEY (cuenta_bancaria_id) REFERENCES cuenta_bancaria (id)
);

-- comprobante_egreso (pago a terceros / proveedores)
CREATE TABLE comprobante_egreso (
    id                   BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    empresa_id           BIGINT        NOT NULL,
    cuenta_bancaria_id   BIGINT,
    beneficiario_id      BIGINT        NOT NULL,        -- tercero
    tipo_documento_id    BIGINT,
    forma_pago_id        BIGINT,
    numero               VARCHAR(40),
    fecha                DATE          NOT NULL,
    valor                NUMERIC(19,4) NOT NULL DEFAULT 0,
    estado               VARCHAR(15)   NOT NULL DEFAULT 'borrador',
    numero_cheque        VARCHAR(50),
    descripcion          VARCHAR(500),
    -- a qué obligación/CxP responde (sin FK cruzada)
    origen_modulo        VARCHAR(30),
    origen_id            BIGINT,
    activo               BOOLEAN       NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, updated_at TIMESTAMP, deleted_at TIMESTAMP,
    usuario_creacion BIGINT, usuario_modificacion BIGINT,
    CONSTRAINT fk_egreso_empresa      FOREIGN KEY (empresa_id)         REFERENCES empresa (id),
    CONSTRAINT fk_egreso_cuenta_banco FOREIGN KEY (cuenta_bancaria_id) REFERENCES cuenta_bancaria (id),
    CONSTRAINT fk_egreso_beneficiario FOREIGN KEY (beneficiario_id)    REFERENCES tercero (id),
    CONSTRAINT fk_egreso_tipo_doc     FOREIGN KEY (tipo_documento_id)  REFERENCES tipo_documento (id),
    CONSTRAINT fk_egreso_forma_pago   FOREIGN KEY (forma_pago_id)      REFERENCES forma_pago (id),
    CONSTRAINT ck_egreso_estado CHECK (estado IN ('borrador','girado','conciliado','anulado'))
);
CREATE INDEX ix_egreso_empresa ON comprobante_egreso (empresa_id);

-- extracto_bancario (← tes_extractos_bancarios)
CREATE TABLE extracto_bancario (
    id                  BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    empresa_id          BIGINT        NOT NULL,
    cuenta_bancaria_id  BIGINT        NOT NULL,
    fecha_inicial       DATE,
    fecha_final         DATE,
    saldo_inicial       NUMERIC(19,4) NOT NULL DEFAULT 0,
    saldo_final         NUMERIC(19,4) NOT NULL DEFAULT 0,
    valor_conciliado    NUMERIC(19,4) NOT NULL DEFAULT 0,
    valor_no_conciliado NUMERIC(19,4) NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, updated_at TIMESTAMP, deleted_at TIMESTAMP,
    CONSTRAINT fk_extracto_empresa FOREIGN KEY (empresa_id)         REFERENCES empresa (id),
    CONSTRAINT fk_extracto_cuenta  FOREIGN KEY (cuenta_bancaria_id) REFERENCES cuenta_bancaria (id)
);

CREATE TABLE extracto_bancario_detalle (
    id                  BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    extracto_bancario_id BIGINT       NOT NULL,
    fecha               DATE,
    descripcion         VARCHAR(500),
    valor               NUMERIC(19,4) NOT NULL DEFAULT 0,
    tipo                VARCHAR(15),                   -- debito | credito
    conciliado          BOOLEAN       NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, deleted_at TIMESTAMP,
    CONSTRAINT fk_extdet_extracto FOREIGN KEY (extracto_bancario_id) REFERENCES extracto_bancario (id)
);
CREATE INDEX ix_extdet_extracto ON extracto_bancario_detalle (extracto_bancario_id);

-- conciliacion_bancaria (← tes_conciliacion_bancaria)
CREATE TABLE conciliacion_bancaria (
    id                     BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    empresa_id             BIGINT        NOT NULL,
    cuenta_bancaria_id     BIGINT        NOT NULL,
    extracto_detalle_id    BIGINT,
    cuenta_contable_id     BIGINT,
    valor_conciliado       NUMERIC(19,4) NOT NULL DEFAULT 0,
    fecha                  DATE,
    origen_modulo          VARCHAR(30),
    origen_id              BIGINT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, deleted_at TIMESTAMP,
    CONSTRAINT fk_concil_empresa  FOREIGN KEY (empresa_id)          REFERENCES empresa (id),
    CONSTRAINT fk_concil_cuenta   FOREIGN KEY (cuenta_bancaria_id)  REFERENCES cuenta_bancaria (id),
    CONSTRAINT fk_concil_extdet   FOREIGN KEY (extracto_detalle_id) REFERENCES extracto_bancario_detalle (id),
    CONSTRAINT fk_concil_contable FOREIGN KEY (cuenta_contable_id)  REFERENCES cuenta (id)
);
CREATE INDEX ix_concil_empresa ON conciliacion_bancaria (empresa_id);
