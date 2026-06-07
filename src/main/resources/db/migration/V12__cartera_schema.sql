-- ============================================================================
-- V12 — Cartera (cuentas por cobrar) — mapeado de cpc_detalles_cartera / cpc_detalles_acuerdos_pago
-- Nyxora · PostgreSQL · esquema public
--
-- Va ANTES de Caja porque el recibo de caja aplica a la cuenta por cobrar.
-- Las edades de cartera son PROYECCIÓN (se calculan desde facturas/recaudos, no se almacenan).
-- Depende de: V1, V5 (tercero), V8 (cuenta), V11 (factura).
-- ============================================================================

-- cuenta_por_cobrar (← cpc_detalles_cartera)
CREATE TABLE cuenta_por_cobrar (
    id                     BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    empresa_id             BIGINT        NOT NULL,
    cliente_id             BIGINT        NOT NULL,      -- tercero
    factura_id             BIGINT,                      -- origen (puede ser nula si es saldo inicial)
    cuenta_id              BIGINT,                      -- ← con_plan_contable_id
    fecha_emision          DATE          NOT NULL,
    fecha_vencimiento      DATE,
    dias                   INT,                         -- ← dias (plazo)
    valor_total            NUMERIC(19,4) NOT NULL DEFAULT 0,
    valor_interes          NUMERIC(19,4) NOT NULL DEFAULT 0,
    saldo                  NUMERIC(19,4) NOT NULL DEFAULT 0,
    fecha_ultima_liquidacion DATE,
    estado                 VARCHAR(15)   NOT NULL DEFAULT 'vigente',
    activo                 BOOLEAN       NOT NULL DEFAULT TRUE,
    created_at             TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at             TIMESTAMP, deleted_at TIMESTAMP,
    usuario_creacion       BIGINT, usuario_modificacion BIGINT,
    CONSTRAINT fk_cxc_empresa FOREIGN KEY (empresa_id) REFERENCES empresa (id),
    CONSTRAINT fk_cxc_cliente FOREIGN KEY (cliente_id) REFERENCES tercero (id),
    CONSTRAINT fk_cxc_factura FOREIGN KEY (factura_id) REFERENCES factura (id),
    CONSTRAINT fk_cxc_cuenta  FOREIGN KEY (cuenta_id)  REFERENCES cuenta (id),
    CONSTRAINT ck_cxc_estado  CHECK (estado IN ('vigente', 'en_acuerdo', 'pagada', 'anulada'))
);
CREATE INDEX ix_cxc_empresa ON cuenta_por_cobrar (empresa_id);
CREATE INDEX ix_cxc_cliente ON cuenta_por_cobrar (cliente_id);
CREATE INDEX ix_cxc_factura ON cuenta_por_cobrar (factura_id);
COMMENT ON TABLE cuenta_por_cobrar IS 'Saldo por cobrar (← cpc_detalles_cartera). Las edades de cartera son proyección recalculable.';

-- acuerdo_pago (encabezado, ← cpc_detalles_acuerdos_pago agrupado)
CREATE TABLE acuerdo_pago (
    id                   BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    empresa_id           BIGINT      NOT NULL,
    cuenta_por_cobrar_id BIGINT      NOT NULL,
    fecha                DATE        NOT NULL,
    numero_cuotas        INT         NOT NULL DEFAULT 1,
    estado               VARCHAR(15) NOT NULL DEFAULT 'vigente',
    activo               BOOLEAN     NOT NULL DEFAULT TRUE,
    created_at           TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at           TIMESTAMP, deleted_at TIMESTAMP,
    usuario_creacion     BIGINT, usuario_modificacion BIGINT,
    CONSTRAINT fk_acuerdo_empresa FOREIGN KEY (empresa_id)           REFERENCES empresa (id),
    CONSTRAINT fk_acuerdo_cxc     FOREIGN KEY (cuenta_por_cobrar_id) REFERENCES cuenta_por_cobrar (id),
    CONSTRAINT ck_acuerdo_estado  CHECK (estado IN ('vigente', 'cumplido', 'incumplido', 'anulado'))
);
CREATE INDEX ix_acuerdo_cxc ON acuerdo_pago (cuenta_por_cobrar_id);

-- acuerdo_pago_cuota (detalle de cuotas, ← cpc_detalles_acuerdos_pago)
CREATE TABLE acuerdo_pago_cuota (
    id               BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    acuerdo_pago_id  BIGINT        NOT NULL,
    numero_cuota     INT           NOT NULL,
    valor            NUMERIC(19,4) NOT NULL,
    fecha_aplicacion DATE,
    estado           VARCHAR(15)   NOT NULL DEFAULT 'pendiente',
    created_at       TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at       TIMESTAMP, deleted_at TIMESTAMP,
    CONSTRAINT fk_cuota_acuerdo FOREIGN KEY (acuerdo_pago_id) REFERENCES acuerdo_pago (id),
    CONSTRAINT uq_cuota_numero  UNIQUE (acuerdo_pago_id, numero_cuota),
    CONSTRAINT ck_cuota_estado  CHECK (estado IN ('pendiente', 'pagada', 'vencida'))
);
CREATE INDEX ix_cuota_acuerdo ON acuerdo_pago_cuota (acuerdo_pago_id);
