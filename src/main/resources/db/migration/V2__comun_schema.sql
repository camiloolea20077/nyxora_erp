-- ============================================================================
-- V2 — Núcleo / maestros + motor mínimo de documentos (módulo Común)
-- ERP MVP · PostgreSQL · Fase 11 (alineado a convención backend v3)
--
-- Depende de V1. Esquema 'public' plano. Multi-tenant por empresa_id.
--
-- Pendiente de validación antes de SEMBRAR datos (no bloquea el esquema):
--   Q3/V8  plan de cuentas e impuestos reales.
--   Q4/V6  reglas de numeración (reinicio por vigencia, prefijos).
--   Q7/V7  precisión monetaria definitiva (aquí NUMERIC(19,4) provisional).
-- ============================================================================

-- ----------------------------------------------------------------------------
-- La familia 'tercero' se movió a V5__comun_terceros.sql (requiere los catálogos
-- propios definidos en V4__comun_catalogos.sql). Aquí quedan los demás maestros.
-- ----------------------------------------------------------------------------

-- ----------------------------------------------------------------------------
-- unidad_medida (catálogo GLOBAL) + producto + impuestos
-- ----------------------------------------------------------------------------
CREATE TABLE unidad_medida (
    id     BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    codigo VARCHAR(10)  NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    CONSTRAINT uq_unidad_medida_codigo UNIQUE (codigo)
);
COMMENT ON TABLE unidad_medida IS 'Catálogo global de unidades de medida. No es multi-tenant.';

-- producto y producto_impuesto se movieron a V6__comun_productos.sql (diseño rico, FK a categoría).

-- impuesto/deducción (← com_impuestos_deducciones): concepto + tarifa por vigencia.
CREATE TABLE impuesto (
    id                   BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    empresa_id           BIGINT        NOT NULL,
    codigo               VARCHAR(20)   NOT NULL,
    nombre               VARCHAR(150)  NOT NULL,
    tipo                 VARCHAR(15)   NOT NULL,            -- iva | retencion | ica | otro (← impuesto_tipo_id)
    causacion            VARCHAR(20),                       -- ← causacion_id (compra/venta/ambas)
    base_gravable        VARCHAR(20),                       -- ← impuesto_base_id
    periodicidad         VARCHAR(20),                       -- ← periodicidad_id
    aplica_aiu           BOOLEAN       NOT NULL DEFAULT FALSE, -- ← aplica_aiu
    retencion_nomina     BOOLEAN       NOT NULL DEFAULT FALSE, -- ← retencion_nomina
    tarifa               NUMERIC(7,4)  NOT NULL,            -- tarifa vigente (detalle por vigencia: ver impuesto_vigencia, futuro)
    vigencia_id          BIGINT        NOT NULL,
    -- Cuentas contables (FK futura a 'cuenta' cuando exista Contabilidad)
    cuenta_compra_id     BIGINT,                            -- ← con_plan_contable_compra_id
    cuenta_venta_id      BIGINT,                            -- ← con_plan_contable_venta_id
    activo               BOOLEAN       NOT NULL DEFAULT TRUE,
    created_at           TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at           TIMESTAMP,
    deleted_at           TIMESTAMP,
    usuario_creacion     BIGINT,
    usuario_modificacion BIGINT,
    CONSTRAINT fk_impuesto_empresa  FOREIGN KEY (empresa_id)  REFERENCES empresa (id),
    CONSTRAINT fk_impuesto_vigencia FOREIGN KEY (vigencia_id) REFERENCES vigencia (id),
    CONSTRAINT uq_impuesto_codigo UNIQUE (empresa_id, codigo, vigencia_id),
    CONSTRAINT ck_impuesto_tipo   CHECK (tipo IN ('iva', 'retencion', 'ica', 'otro')),
    CONSTRAINT ck_impuesto_tarifa CHECK (tarifa >= 0)
);
CREATE INDEX ix_impuesto_empresa ON impuesto (empresa_id);
COMMENT ON TABLE impuesto IS 'Impuesto/retención (← com_impuestos_deducciones). Tarifa por vigencia; cuentas contables se enlazan en la fase de Contabilidad.';

-- centro_costo se movió a V7__comun_organizacion.sql (diseño rico jerárquico).

-- ----------------------------------------------------------------------------
-- lista / lista_item (catálogos tipados — reemplazan EAV)
-- ----------------------------------------------------------------------------
CREATE TABLE lista (
    id                   BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    empresa_id           BIGINT       NOT NULL,
    codigo               VARCHAR(50)  NOT NULL,
    nombre               VARCHAR(255) NOT NULL,
    activo               BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at           TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at           TIMESTAMP,
    deleted_at           TIMESTAMP,
    usuario_creacion     BIGINT,
    usuario_modificacion BIGINT,
    CONSTRAINT fk_lista_empresa FOREIGN KEY (empresa_id) REFERENCES empresa (id),
    CONSTRAINT uq_lista_codigo  UNIQUE (empresa_id, codigo)
);
CREATE INDEX ix_lista_empresa ON lista (empresa_id);

CREATE TABLE lista_item (
    id       BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    lista_id BIGINT       NOT NULL,
    codigo   VARCHAR(50)  NOT NULL,
    valor    VARCHAR(255) NOT NULL,
    orden    INT          NOT NULL DEFAULT 0,
    activo   BOOLEAN      NOT NULL DEFAULT TRUE,
    CONSTRAINT fk_lista_item_lista  FOREIGN KEY (lista_id) REFERENCES lista (id),
    CONSTRAINT uq_lista_item_codigo UNIQUE (lista_id, codigo)
);
COMMENT ON TABLE lista_item IS 'Catálogo tipado con FK real (no EAV con id mágico).';

-- ----------------------------------------------------------------------------
-- tipo_documento + consecutivo (numeración transaccional)
-- ----------------------------------------------------------------------------
CREATE TABLE tipo_documento (
    id                    BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    empresa_id            BIGINT       NOT NULL,
    modulo                VARCHAR(30)  NOT NULL,
    codigo                VARCHAR(20)  NOT NULL,
    nombre                VARCHAR(255) NOT NULL,
    prefijo               VARCHAR(10),
    reinicia_por_vigencia BOOLEAN      NOT NULL DEFAULT TRUE,
    activo                BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at            TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at            TIMESTAMP,
    deleted_at            TIMESTAMP,
    usuario_creacion      BIGINT,
    usuario_modificacion  BIGINT,
    CONSTRAINT fk_tipo_documento_empresa FOREIGN KEY (empresa_id) REFERENCES empresa (id),
    CONSTRAINT uq_tipo_documento_codigo  UNIQUE (empresa_id, codigo)
);
CREATE INDEX ix_tipo_documento_empresa ON tipo_documento (empresa_id);
COMMENT ON COLUMN tipo_documento.modulo IS 'Módulo dueño del tipo (compras, facturacion, caja, ...).';

CREATE TABLE consecutivo (
    id                BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    tipo_documento_id BIGINT NOT NULL,
    sede_id           BIGINT NOT NULL,
    vigencia_id       BIGINT NOT NULL,
    ultimo_numero     BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT fk_consecutivo_tipo_documento FOREIGN KEY (tipo_documento_id) REFERENCES tipo_documento (id),
    CONSTRAINT fk_consecutivo_sede           FOREIGN KEY (sede_id)           REFERENCES sede (id),
    CONSTRAINT fk_consecutivo_vigencia       FOREIGN KEY (vigencia_id)       REFERENCES vigencia (id),
    CONSTRAINT uq_consecutivo UNIQUE (tipo_documento_id, sede_id, vigencia_id),
    CONSTRAINT ck_consecutivo_no_negativo CHECK (ultimo_numero >= 0)
);
COMMENT ON TABLE consecutivo IS 'Numeración única por (tipo, sede, vigencia). Se incrementa con SELECT ... FOR UPDATE.';
