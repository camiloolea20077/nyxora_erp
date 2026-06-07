-- ============================================================================
-- V19 — Contratación — mapeado de ctr_contratos / ctr_modalidades /
--      ctr_contratos_detalles / ctr_plantillas_clausulas / ctr_tipos_contratos_*
-- Nyxora · PostgreSQL · esquema public
--
-- El ctr_contratos real es delgado (contexto salud); aquí se modela el contrato de
-- adquisición limpio (diseño Fase 9) con modalidad, cláusulas y pólizas.
-- Depende de: V1, V5 (tercero), V18 (poliza_seguro).
-- ============================================================================

-- modalidad (← ctr_modalidades)
CREATE TABLE modalidad_contrato (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    empresa_id BIGINT NOT NULL,
    codigo VARCHAR(50) NOT NULL,
    nombre VARCHAR(150) NOT NULL,
    descripcion TEXT,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, updated_at TIMESTAMP, deleted_at TIMESTAMP,
    CONSTRAINT fk_modalidad_empresa FOREIGN KEY (empresa_id) REFERENCES empresa (id),
    CONSTRAINT uq_modalidad_codigo UNIQUE (empresa_id, codigo)
);

-- clausula_plantilla (← ctr_plantillas_clausulas)
CREATE TABLE clausula_plantilla (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    empresa_id BIGINT NOT NULL,
    tipo_clausula VARCHAR(40),
    numero VARCHAR(50),
    orden VARCHAR(50),
    nombre VARCHAR(150),
    texto TEXT,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, updated_at TIMESTAMP, deleted_at TIMESTAMP,
    CONSTRAINT fk_clausula_plantilla_empresa FOREIGN KEY (empresa_id) REFERENCES empresa (id)
);

-- contrato (← ctr_contratos, enriquecido para adquisición)
CREATE TABLE contrato (
    id                   BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    empresa_id           BIGINT       NOT NULL,
    numero               VARCHAR(40),
    nombre               VARCHAR(191) NOT NULL,
    tipo_contrato        VARCHAR(40),                  -- ← tipo_contrato_id
    contratista_id       BIGINT,                       -- tercero
    modalidad_id         BIGINT,
    objeto               TEXT,
    fecha_inicio         DATE,
    fecha_fin            DATE,
    valor                NUMERIC(19,4),
    estado               VARCHAR(20)  NOT NULL DEFAULT 'planeado',
    activo               BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, updated_at TIMESTAMP, deleted_at TIMESTAMP,
    usuario_creacion BIGINT, usuario_modificacion BIGINT,
    CONSTRAINT fk_contrato_empresa     FOREIGN KEY (empresa_id)     REFERENCES empresa (id),
    CONSTRAINT fk_contrato_contratista FOREIGN KEY (contratista_id) REFERENCES tercero (id),
    CONSTRAINT fk_contrato_modalidad   FOREIGN KEY (modalidad_id)   REFERENCES modalidad_contrato (id),
    CONSTRAINT ck_contrato_estado CHECK (estado IN ('planeado','adjudicado','suscrito','en_ejecucion','liquidado','anulado'))
);
CREATE INDEX ix_contrato_empresa ON contrato (empresa_id);

-- contrato_clausula (← ctr_contratos_detalles)
CREATE TABLE contrato_clausula (
    id           BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    contrato_id  BIGINT NOT NULL,
    numero       VARCHAR(50),
    orden        VARCHAR(50),
    nombre       VARCHAR(150),
    texto        TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, deleted_at TIMESTAMP,
    CONSTRAINT fk_contrato_clausula_contrato FOREIGN KEY (contrato_id) REFERENCES contrato (id)
);
CREATE INDEX ix_contrato_clausula_contrato ON contrato_clausula (contrato_id);

-- contrato_poliza (pólizas del contrato → reutiliza poliza_seguro de V18)
CREATE TABLE contrato_poliza (
    id               BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    contrato_id      BIGINT NOT NULL,
    poliza_seguro_id BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, deleted_at TIMESTAMP,
    CONSTRAINT fk_contrato_pol_contrato FOREIGN KEY (contrato_id)      REFERENCES contrato (id),
    CONSTRAINT fk_contrato_pol_poliza   FOREIGN KEY (poliza_seguro_id) REFERENCES poliza_seguro (id),
    CONSTRAINT uq_contrato_poliza UNIQUE (contrato_id, poliza_seguro_id)
);
