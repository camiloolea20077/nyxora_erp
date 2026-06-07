-- ============================================================================
-- V15 — Presupuesto (sector público) — mapeado de pre_planes_presupuestales /
--      pre_detalles_presupuestales / pre_fuentes_financiamientos / pre_cpcs
-- Nyxora · PostgreSQL · esquema public
--
-- El 'plan presupuestal' real mezcla la DEFINICIÓN del rubro con sus AGREGADOS (plan_inicial,
-- adiciones, compromiso, obligacion, pagado, PAC...). Aquí se separa: rubro_presupuestal
-- (definición) + saldo_presupuestal (proyección recalculable) + pac. Las operaciones
-- (CDP/compromiso/obligación/pago) son afectacion_presupuestal append-only.
-- Depende de: V1, V5 (tercero), V7 (centro_costo, proyecto), V14 (recurso).
-- ============================================================================

-- fuente_financiamiento (← pre_fuentes_financiamientos)
CREATE TABLE fuente_financiamiento (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    empresa_id BIGINT NOT NULL,
    codigo VARCHAR(45) NOT NULL,
    nombre VARCHAR(150) NOT NULL,
    descripcion TEXT,
    tipo_recurso VARCHAR(30),                          -- ← tipo_recurso_id
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, updated_at TIMESTAMP, deleted_at TIMESTAMP,
    CONSTRAINT fk_fuente_empresa FOREIGN KEY (empresa_id) REFERENCES empresa (id),
    CONSTRAINT uq_fuente_codigo UNIQUE (empresa_id, codigo)
);

-- cpc (← pre_cpcs) — Clasificador de Productos y servicios presupuestal, jerárquico
CREATE TABLE cpc (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    empresa_id BIGINT NOT NULL,
    vigencia_id BIGINT,
    cpc_padre_id BIGINT,
    codigo VARCHAR(40) NOT NULL,
    nombre VARCHAR(250) NOT NULL,
    maneja_movimiento BOOLEAN NOT NULL DEFAULT FALSE,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, updated_at TIMESTAMP, deleted_at TIMESTAMP,
    CONSTRAINT fk_cpc_empresa  FOREIGN KEY (empresa_id)  REFERENCES empresa (id),
    CONSTRAINT fk_cpc_vigencia FOREIGN KEY (vigencia_id) REFERENCES vigencia (id),
    CONSTRAINT fk_cpc_padre    FOREIGN KEY (cpc_padre_id) REFERENCES cpc (id),
    CONSTRAINT uq_cpc_codigo   UNIQUE (empresa_id, codigo)
);

-- rubro_presupuestal (← pre_planes_presupuestales; solo DEFINICIÓN, jerárquico nested set)
CREATE TABLE rubro_presupuestal (
    id                       BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    empresa_id               BIGINT       NOT NULL,
    vigencia_id              BIGINT       NOT NULL,
    rubro_padre_id           BIGINT,
    tipo_rubro               VARCHAR(20),                  -- ← tipo_rubro_id: ingreso | gasto
    codigo_rubro             VARCHAR(40)  NOT NULL,
    nombre_rubro             TEXT         NOT NULL,
    maneja_movimiento        BOOLEAN      NOT NULL DEFAULT FALSE,
    homologacion_circular_unica VARCHAR(10),
    izquierda INT, derecha INT, nivel INT,
    activo                   BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, updated_at TIMESTAMP, deleted_at TIMESTAMP,
    usuario_creacion BIGINT, usuario_modificacion BIGINT,
    CONSTRAINT fk_rubro_empresa  FOREIGN KEY (empresa_id)     REFERENCES empresa (id),
    CONSTRAINT fk_rubro_vigencia FOREIGN KEY (vigencia_id)    REFERENCES vigencia (id),
    CONSTRAINT fk_rubro_padre    FOREIGN KEY (rubro_padre_id) REFERENCES rubro_presupuestal (id),
    CONSTRAINT uq_rubro_codigo   UNIQUE (empresa_id, vigencia_id, codigo_rubro)
);
CREATE INDEX ix_rubro_empresa ON rubro_presupuestal (empresa_id);

-- afectacion_presupuestal (← pre_detalles_presupuestales; cadena de ejecución, APPEND-ONLY)
CREATE TABLE afectacion_presupuestal (
    id                       BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    empresa_id               BIGINT        NOT NULL,
    rubro_presupuestal_id    BIGINT        NOT NULL,
    tipo_operacion           VARCHAR(20)   NOT NULL,        -- disponibilidad|compromiso|obligacion|pago|reconocimiento|recaudo
    tercero_id               BIGINT,
    centro_costo_id          BIGINT,
    proyecto_id              BIGINT,
    fuente_financiamiento_id BIGINT,
    cpc_id                   BIGINT,
    descripcion              TEXT,
    valor                    NUMERIC(19,4) NOT NULL DEFAULT 0,
    subtotal                 NUMERIC(19,4),
    saldo                    NUMERIC(19,4),
    origen_modulo            VARCHAR(30),
    origen_id                BIGINT,
    created_at               TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    usuario_creacion         BIGINT,
    CONSTRAINT fk_afect_empresa      FOREIGN KEY (empresa_id)               REFERENCES empresa (id),
    CONSTRAINT fk_afect_rubro        FOREIGN KEY (rubro_presupuestal_id)    REFERENCES rubro_presupuestal (id),
    CONSTRAINT fk_afect_tercero      FOREIGN KEY (tercero_id)               REFERENCES tercero (id),
    CONSTRAINT fk_afect_centro_costo FOREIGN KEY (centro_costo_id)          REFERENCES centro_costo (id),
    CONSTRAINT fk_afect_proyecto     FOREIGN KEY (proyecto_id)              REFERENCES proyecto (id),
    CONSTRAINT fk_afect_fuente       FOREIGN KEY (fuente_financiamiento_id) REFERENCES fuente_financiamiento (id),
    CONSTRAINT fk_afect_cpc          FOREIGN KEY (cpc_id)                   REFERENCES cpc (id),
    CONSTRAINT ck_afect_tipo CHECK (tipo_operacion IN ('disponibilidad','compromiso','obligacion','pago','reconocimiento','recaudo'))
);
CREATE INDEX ix_afect_empresa ON afectacion_presupuestal (empresa_id);
CREATE INDEX ix_afect_rubro   ON afectacion_presupuestal (rubro_presupuestal_id);
COMMENT ON TABLE afectacion_presupuestal IS 'Cadena CDP→compromiso→obligación→pago (append-only). Disponibilidad = saldo recalculable.';

-- saldo_presupuestal (← agregados de pre_planes_presupuestales; PROYECCIÓN recalculable)
CREATE TABLE saldo_presupuestal (
    id                    BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    empresa_id            BIGINT        NOT NULL,
    rubro_presupuestal_id BIGINT        NOT NULL,
    anio                  INT           NOT NULL,
    mes                   INT,
    plan_inicial          NUMERIC(19,4) NOT NULL DEFAULT 0,
    adiciones             NUMERIC(19,4) NOT NULL DEFAULT 0,
    reducciones           NUMERIC(19,4) NOT NULL DEFAULT 0,
    aplazamientos         NUMERIC(19,4) NOT NULL DEFAULT 0,
    creditos              NUMERIC(19,4) NOT NULL DEFAULT 0,
    contra_creditos       NUMERIC(19,4) NOT NULL DEFAULT 0,
    disponibilidad        NUMERIC(19,4) NOT NULL DEFAULT 0,
    compromiso            NUMERIC(19,4) NOT NULL DEFAULT 0,
    obligacion            NUMERIC(19,4) NOT NULL DEFAULT 0,
    pagado                NUMERIC(19,4) NOT NULL DEFAULT 0,
    reconocimientos       NUMERIC(19,4) NOT NULL DEFAULT 0,
    recaudos              NUMERIC(19,4) NOT NULL DEFAULT 0,
    fecha_recalculo       TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_saldopre_empresa FOREIGN KEY (empresa_id)            REFERENCES empresa (id),
    CONSTRAINT fk_saldopre_rubro   FOREIGN KEY (rubro_presupuestal_id) REFERENCES rubro_presupuestal (id),
    CONSTRAINT uq_saldo_presupuestal UNIQUE (rubro_presupuestal_id, anio, mes)
);

-- pac (Plan Anualizado de Caja, ← pac01..pac12)
CREATE TABLE pac_presupuestal (
    id                    BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    empresa_id            BIGINT        NOT NULL,
    rubro_presupuestal_id BIGINT        NOT NULL,
    anio                  INT           NOT NULL,
    mes                   INT           NOT NULL,
    valor                 NUMERIC(19,4) NOT NULL DEFAULT 0,
    CONSTRAINT fk_pac_empresa FOREIGN KEY (empresa_id)            REFERENCES empresa (id),
    CONSTRAINT fk_pac_rubro   FOREIGN KEY (rubro_presupuestal_id) REFERENCES rubro_presupuestal (id),
    CONSTRAINT uq_pac UNIQUE (rubro_presupuestal_id, anio, mes),
    CONSTRAINT ck_pac_mes CHECK (mes BETWEEN 1 AND 12)
);
