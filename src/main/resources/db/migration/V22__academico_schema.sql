-- ============================================================================
-- V22 — Académico (educación superior) — mapeado de aca_*
-- La carga docente alimenta la nómina de catedráticos (vinculacion V21).
-- Depende de: V1, V5, V7 (centro_costo), V20 (nivel_estudio), V21 (vinculacion).
-- ============================================================================

CREATE TABLE institucion_snies (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    codigo VARCHAR(20) NOT NULL, nombre VARCHAR(255) NOT NULL, activo BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT uq_institucion_snies_codigo UNIQUE (codigo)
);

CREATE TABLE programa_academico (
    id                   BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    empresa_id           BIGINT       NOT NULL,
    codigo               VARCHAR(191),
    nombre               VARCHAR(191) NOT NULL,
    tipo_programa        VARCHAR(40),                  -- ← tipo_programa_academico_id
    modalidad            VARCHAR(40),                  -- ← modalidad_programa_id
    centro_costo_programa_id  BIGINT,                  -- ← com_centro_costo_programa_id
    centro_costo_facultad_id  BIGINT,                  -- ← com_centro_costo_facultad_id
    registro_academico   VARCHAR(191),
    descripcion          TEXT,
    activo               BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, updated_at TIMESTAMP, deleted_at TIMESTAMP,
    CONSTRAINT fk_programa_empresa   FOREIGN KEY (empresa_id)               REFERENCES empresa (id),
    CONSTRAINT fk_programa_cc_prog   FOREIGN KEY (centro_costo_programa_id) REFERENCES centro_costo (id),
    CONSTRAINT fk_programa_cc_fac    FOREIGN KEY (centro_costo_facultad_id) REFERENCES centro_costo (id)
);
CREATE INDEX ix_programa_empresa ON programa_academico (empresa_id);

CREATE TABLE asignatura (
    id                   BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    empresa_id           BIGINT       NOT NULL,
    codigo               VARCHAR(20)  NOT NULL,
    nombre               VARCHAR(191) NOT NULL,
    descripcion          TEXT,
    centro_costo_departamento_id BIGINT,
    activo               BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, updated_at TIMESTAMP, deleted_at TIMESTAMP,
    CONSTRAINT fk_asignatura_empresa FOREIGN KEY (empresa_id) REFERENCES empresa (id),
    CONSTRAINT fk_asignatura_cc_dpto FOREIGN KEY (centro_costo_departamento_id) REFERENCES centro_costo (id),
    CONSTRAINT uq_asignatura_codigo  UNIQUE (empresa_id, codigo)
);

CREATE TABLE asignatura_programa (
    id            BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    asignatura_id BIGINT NOT NULL,
    programa_academico_id BIGINT NOT NULL,
    semestre      INT,
    creditos      INT,
    activo        BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, deleted_at TIMESTAMP,
    CONSTRAINT fk_asig_prog_asignatura FOREIGN KEY (asignatura_id)         REFERENCES asignatura (id),
    CONSTRAINT fk_asig_prog_programa   FOREIGN KEY (programa_academico_id) REFERENCES programa_academico (id),
    CONSTRAINT uq_asignatura_programa  UNIQUE (asignatura_id, programa_academico_id)
);

CREATE TABLE grupo_academico (
    id                   BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    empresa_id           BIGINT NOT NULL,
    programa_academico_id BIGINT,
    codigo               VARCHAR(20), nombre VARCHAR(150),
    periodo              VARCHAR(20),
    activo               BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, updated_at TIMESTAMP, deleted_at TIMESTAMP,
    CONSTRAINT fk_grupo_acad_empresa  FOREIGN KEY (empresa_id)            REFERENCES empresa (id),
    CONSTRAINT fk_grupo_acad_programa FOREIGN KEY (programa_academico_id) REFERENCES programa_academico (id)
);

-- carga_academica (← aca_cargas_academicas) — carga docente de un catedrático
CREATE TABLE carga_academica (
    id               BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    empresa_id       BIGINT NOT NULL,
    vinculacion_id   BIGINT,                           -- ← nom_vinculacion_id (docente)
    nivel_estudio_id BIGINT,
    numero_acto_administrativo VARCHAR(45),
    fecha_acto_administrativo  DATE,
    activo           BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, updated_at TIMESTAMP, deleted_at TIMESTAMP,
    CONSTRAINT fk_carga_empresa     FOREIGN KEY (empresa_id)       REFERENCES empresa (id),
    CONSTRAINT fk_carga_vinculacion FOREIGN KEY (vinculacion_id)   REFERENCES vinculacion (id),
    CONSTRAINT fk_carga_nivel       FOREIGN KEY (nivel_estudio_id) REFERENCES nivel_estudio (id)
);
CREATE INDEX ix_carga_vinculacion ON carga_academica (vinculacion_id);

CREATE TABLE carga_academica_detalle (
    id                    BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    carga_academica_id    BIGINT NOT NULL,
    asignatura_programa_id BIGINT,
    grupo_academico_id    BIGINT,
    horas                 NUMERIC(9,2),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, deleted_at TIMESTAMP,
    CONSTRAINT fk_carga_det_carga    FOREIGN KEY (carga_academica_id)     REFERENCES carga_academica (id),
    CONSTRAINT fk_carga_det_asigprog FOREIGN KEY (asignatura_programa_id) REFERENCES asignatura_programa (id),
    CONSTRAINT fk_carga_det_grupo    FOREIGN KEY (grupo_academico_id)     REFERENCES grupo_academico (id)
);
CREATE INDEX ix_carga_det_carga ON carga_academica_detalle (carga_academica_id);
