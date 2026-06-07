-- ============================================================================
-- V8 — Contabilidad básica — mapeado de con_plan_contable / con_detalles_contables
-- Nyxora · PostgreSQL · esquema public
--
-- Sumidero de interfaces: los demás módulos generan comprobantes por evento de dominio.
-- Mejoras sobre el real:
--   • Encabezado 'comprobante' explícito (el real colgaba los detalles del documento universal).
--   • 'movimiento_contable' con DÉBITO/CRÉDITO explícitos (no 'valor' con signo) y APPEND-ONLY.
--   • Saldos como PROYECCIÓN recalculable (no fuente de verdad).
-- Depende de: V1 (empresa, vigencia), V2 (tipo_documento, impuesto), V5 (tercero), V7 (centro_costo, proyecto).
-- ============================================================================

-- ----------------------------------------------------------------------------
-- cuenta (plan contable, ← con_plan_contable) — jerárquico nested set
-- ----------------------------------------------------------------------------
CREATE TABLE cuenta (
    id                       BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    empresa_id               BIGINT       NOT NULL,
    cuenta_padre_id          BIGINT,                       -- ← con_plan_contable_id
    codigo_cuenta            VARCHAR(15)  NOT NULL,        -- ← codigo_cuenta
    nombre_cuenta            VARCHAR(200) NOT NULL,        -- ← nombre_cuenta
    nivel                    INT,                          -- ← nivel
    izquierda                INT,                          -- ← izquierda (nested set)
    derecha                  INT,                          -- ← derecha
    naturaleza               VARCHAR(10)  NOT NULL,        -- ← naturaleza_id: debito | credito
    tipo_cuenta              VARCHAR(20),                  -- ← tipo_cuenta_id (clase: activo/pasivo/...)
    maneja_movimiento        BOOLEAN      NOT NULL DEFAULT FALSE, -- ← maneja_movimiento (cuenta auxiliar/hoja)
    maneja_movimiento_manual BOOLEAN      NOT NULL DEFAULT FALSE, -- ← maneja_movimiento_manual
    maneja_tercero           BOOLEAN      NOT NULL DEFAULT FALSE, -- ← maneja_tercero
    maneja_centro_costo      BOOLEAN      NOT NULL DEFAULT FALSE, -- ← maneja_centro_costo
    maneja_impuesto          BOOLEAN      NOT NULL DEFAULT FALSE, -- ← maneja_impuesto
    maneja_proyecto          BOOLEAN      NOT NULL DEFAULT FALSE, -- ← maneja_proyecto
    maneja_recurso           BOOLEAN      NOT NULL DEFAULT FALSE, -- ← maneja_recurso
    maneja_saldo_contrario   BOOLEAN      NOT NULL DEFAULT FALSE, -- ← maneja_saldo_contrario
    es_corriente             BOOLEAN,                      -- ← corriente
    activo                   BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at               TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at               TIMESTAMP,
    deleted_at               TIMESTAMP,
    usuario_creacion         BIGINT,
    usuario_modificacion     BIGINT,
    CONSTRAINT fk_cuenta_empresa FOREIGN KEY (empresa_id)      REFERENCES empresa (id),
    CONSTRAINT fk_cuenta_padre   FOREIGN KEY (cuenta_padre_id) REFERENCES cuenta (id),
    CONSTRAINT uq_cuenta_codigo  UNIQUE (empresa_id, codigo_cuenta),
    CONSTRAINT ck_cuenta_naturaleza CHECK (naturaleza IN ('debito', 'credito'))
);
CREATE INDEX ix_cuenta_empresa ON cuenta (empresa_id);
CREATE INDEX ix_cuenta_padre   ON cuenta (cuenta_padre_id);
COMMENT ON COLUMN cuenta.maneja_movimiento IS 'TRUE: cuenta auxiliar (hoja) que recibe movimientos; FALSE: cuenta mayor agrupadora.';

-- ----------------------------------------------------------------------------
-- periodo_contable (← agno/mes del real) — máquina de estados de cierre
-- ----------------------------------------------------------------------------
CREATE TABLE periodo_contable (
    id                   BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    empresa_id           BIGINT      NOT NULL,
    vigencia_id          BIGINT      NOT NULL,
    anio                 INT         NOT NULL,             -- ← agno
    mes                  INT         NOT NULL,             -- ← mes
    estado               VARCHAR(15) NOT NULL DEFAULT 'abierto',
    fecha_cierre         TIMESTAMP,
    created_at           TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at           TIMESTAMP,
    usuario_creacion     BIGINT,
    usuario_modificacion BIGINT,
    CONSTRAINT fk_periodo_empresa  FOREIGN KEY (empresa_id)  REFERENCES empresa (id),
    CONSTRAINT fk_periodo_vigencia FOREIGN KEY (vigencia_id) REFERENCES vigencia (id),
    CONSTRAINT uq_periodo          UNIQUE (empresa_id, anio, mes),
    CONSTRAINT ck_periodo_estado   CHECK (estado IN ('abierto', 'cerrado')),
    CONSTRAINT ck_periodo_mes      CHECK (mes BETWEEN 1 AND 12)
);
CREATE INDEX ix_periodo_empresa ON periodo_contable (empresa_id);

-- ----------------------------------------------------------------------------
-- comprobante (encabezado del asiento contable)
-- ----------------------------------------------------------------------------
CREATE TABLE comprobante (
    id                   BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    empresa_id           BIGINT        NOT NULL,
    periodo_contable_id  BIGINT        NOT NULL,
    tipo_documento_id    BIGINT,
    numero               VARCHAR(40),
    fecha                DATE          NOT NULL,
    descripcion          VARCHAR(500),
    estado               VARCHAR(15)   NOT NULL DEFAULT 'borrador',
    total_debito         NUMERIC(19,4) NOT NULL DEFAULT 0,
    total_credito        NUMERIC(19,4) NOT NULL DEFAULT 0,
    -- Trazabilidad del origen (qué módulo/documento lo generó), sin FK cruzada
    origen_modulo        VARCHAR(30),
    origen_id            BIGINT,
    activo               BOOLEAN       NOT NULL DEFAULT TRUE,
    created_at           TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at           TIMESTAMP,
    deleted_at           TIMESTAMP,
    usuario_creacion     BIGINT,
    usuario_modificacion BIGINT,
    CONSTRAINT fk_comprobante_empresa  FOREIGN KEY (empresa_id)          REFERENCES empresa (id),
    CONSTRAINT fk_comprobante_periodo  FOREIGN KEY (periodo_contable_id) REFERENCES periodo_contable (id),
    CONSTRAINT fk_comprobante_tipo_doc FOREIGN KEY (tipo_documento_id)   REFERENCES tipo_documento (id),
    CONSTRAINT ck_comprobante_estado   CHECK (estado IN ('borrador', 'confirmado', 'reversado'))
);
CREATE INDEX ix_comprobante_empresa ON comprobante (empresa_id);
CREATE INDEX ix_comprobante_periodo ON comprobante (periodo_contable_id);
CREATE INDEX ix_comprobante_origen  ON comprobante (origen_modulo, origen_id);

-- ----------------------------------------------------------------------------
-- movimiento_contable (← con_detalles_contables) — APPEND-ONLY (sin updated_at/deleted_at)
-- ----------------------------------------------------------------------------
CREATE TABLE movimiento_contable (
    id                       BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    empresa_id               BIGINT        NOT NULL,
    comprobante_id           BIGINT        NOT NULL,
    cuenta_id                BIGINT        NOT NULL,        -- ← con_plan_contable_id
    tercero_id               BIGINT,                        -- ← com_tercero_id
    centro_costo_id          BIGINT,                        -- ← com_centro_costo_id
    proyecto_id              BIGINT,                        -- ← pre_proyecto_id
    recurso_id               BIGINT,                        -- ← cos_recurso_id (FK futura)
    descripcion              VARCHAR(500),                  -- ← descripcion
    debito                   NUMERIC(19,4) NOT NULL DEFAULT 0,
    credito                  NUMERIC(19,4) NOT NULL DEFAULT 0,
    valor_base               NUMERIC(19,4),                 -- ← valor_base (base gravable)
    impuesto_id              BIGINT,                        -- ← com_impuesto_deduccion_id
    porcentaje_impuesto      NUMERIC(7,4),                  -- ← porcentaje_impuesto_deduccion
    valor_trm                NUMERIC(19,4),                 -- ← valor_trm (multimoneda)
    valor_dolar              NUMERIC(19,4),                 -- ← valor_dolar
    created_at               TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    usuario_creacion         BIGINT,
    CONSTRAINT fk_mov_comprobante  FOREIGN KEY (comprobante_id)  REFERENCES comprobante (id),
    CONSTRAINT fk_mov_cuenta       FOREIGN KEY (cuenta_id)       REFERENCES cuenta (id),
    CONSTRAINT fk_mov_tercero      FOREIGN KEY (tercero_id)      REFERENCES tercero (id),
    CONSTRAINT fk_mov_centro_costo FOREIGN KEY (centro_costo_id) REFERENCES centro_costo (id),
    CONSTRAINT fk_mov_proyecto     FOREIGN KEY (proyecto_id)     REFERENCES proyecto (id),
    CONSTRAINT fk_mov_impuesto     FOREIGN KEY (impuesto_id)     REFERENCES impuesto (id),
    CONSTRAINT ck_mov_debito_credito CHECK (debito >= 0 AND credito >= 0)
);
CREATE INDEX ix_mov_empresa     ON movimiento_contable (empresa_id);
CREATE INDEX ix_mov_comprobante ON movimiento_contable (comprobante_id);
CREATE INDEX ix_mov_cuenta      ON movimiento_contable (cuenta_id);
COMMENT ON TABLE movimiento_contable IS 'Partida contable inmutable (append-only). Un error se corrige con un comprobante de reversa, no editando.';

-- ----------------------------------------------------------------------------
-- saldo_contable (← con_saldos_*) — PROYECCIÓN recalculable desde movimientos
-- ----------------------------------------------------------------------------
CREATE TABLE saldo_contable (
    id                    BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    empresa_id            BIGINT        NOT NULL,
    periodo_contable_id   BIGINT        NOT NULL,
    cuenta_id             BIGINT        NOT NULL,
    tercero_id            BIGINT,                           -- eje opcional
    centro_costo_id       BIGINT,                           -- eje opcional
    saldo_debito_anterior  NUMERIC(19,4) NOT NULL DEFAULT 0,
    saldo_credito_anterior NUMERIC(19,4) NOT NULL DEFAULT 0,
    debito_periodo         NUMERIC(19,4) NOT NULL DEFAULT 0,
    credito_periodo        NUMERIC(19,4) NOT NULL DEFAULT 0,
    saldo_debito_final     NUMERIC(19,4) NOT NULL DEFAULT 0,
    saldo_credito_final    NUMERIC(19,4) NOT NULL DEFAULT 0,
    fecha_recalculo        TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_saldo_empresa      FOREIGN KEY (empresa_id)          REFERENCES empresa (id),
    CONSTRAINT fk_saldo_periodo      FOREIGN KEY (periodo_contable_id) REFERENCES periodo_contable (id),
    CONSTRAINT fk_saldo_cuenta       FOREIGN KEY (cuenta_id)           REFERENCES cuenta (id),
    CONSTRAINT fk_saldo_tercero      FOREIGN KEY (tercero_id)          REFERENCES tercero (id),
    CONSTRAINT fk_saldo_centro_costo FOREIGN KEY (centro_costo_id)     REFERENCES centro_costo (id),
    CONSTRAINT uq_saldo_contable     UNIQUE (periodo_contable_id, cuenta_id, tercero_id, centro_costo_id)
);
CREATE INDEX ix_saldo_empresa ON saldo_contable (empresa_id);
COMMENT ON TABLE saldo_contable IS 'Proyección recalculable desde movimiento_contable. NO es fuente de verdad (se reconstruye).';
