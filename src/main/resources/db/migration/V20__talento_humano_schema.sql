-- ============================================================================
-- V20 — Talento Humano — mapeado de thu_* . El EMPLEADO es un tercero (rol empleado);
-- estas son sus tablas satélite + evaluación de desempeño.
-- Depende de: V1, V4 (municipio), V5 (tercero).
-- ============================================================================

-- nivel_estudio (catálogo, ← thu_niveles_estudios)
CREATE TABLE nivel_estudio (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    codigo VARCHAR(10) NOT NULL, nombre VARCHAR(150) NOT NULL, activo BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT uq_nivel_estudio_codigo UNIQUE (codigo)
);

-- empleado_estudio (← thu_empleados_estudios)
CREATE TABLE empleado_estudio (
    id                   BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    empresa_id           BIGINT NOT NULL,
    empleado_id          BIGINT NOT NULL,              -- ← com_tercero_empleado_id
    nivel_estudio_id     BIGINT,
    institucion          VARCHAR(191),
    titulo               VARCHAR(191),
    fecha_inicial        DATE, fecha_final DATE, fecha_grado DATE,
    numero_tarjeta_profesional VARCHAR(50),
    municipio_estudio_id BIGINT,
    semestres_aprobados  SMALLINT,
    convalidado          BOOLEAN NOT NULL DEFAULT FALSE,
    activo               BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, updated_at TIMESTAMP, deleted_at TIMESTAMP,
    CONSTRAINT fk_emp_estudio_empresa   FOREIGN KEY (empresa_id)           REFERENCES empresa (id),
    CONSTRAINT fk_emp_estudio_empleado  FOREIGN KEY (empleado_id)          REFERENCES tercero (id),
    CONSTRAINT fk_emp_estudio_nivel     FOREIGN KEY (nivel_estudio_id)     REFERENCES nivel_estudio (id),
    CONSTRAINT fk_emp_estudio_municipio FOREIGN KEY (municipio_estudio_id) REFERENCES municipio (id)
);
CREATE INDEX ix_emp_estudio_empleado ON empleado_estudio (empleado_id);

-- empleado_familiar (← thu_empleados_familiares)
CREATE TABLE empleado_familiar (
    id                   BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    empresa_id           BIGINT NOT NULL,
    empleado_id          BIGINT NOT NULL,
    nombre_apellido      VARCHAR(255) NOT NULL,
    fecha_nacimiento     DATE,
    parentesco           VARCHAR(40),                  -- ← parentesco_id
    a_cargo              BOOLEAN NOT NULL DEFAULT FALSE,
    vivo                 BOOLEAN NOT NULL DEFAULT TRUE,
    convive              BOOLEAN NOT NULL DEFAULT FALSE,
    dependiente_retencion BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, updated_at TIMESTAMP, deleted_at TIMESTAMP,
    CONSTRAINT fk_emp_fam_empresa  FOREIGN KEY (empresa_id)  REFERENCES empresa (id),
    CONSTRAINT fk_emp_fam_empleado FOREIGN KEY (empleado_id) REFERENCES tercero (id)
);
CREATE INDEX ix_emp_familiar_empleado ON empleado_familiar (empleado_id);

-- empleado_historia_laboral (← thu_empleados_historias_laborales)
CREATE TABLE empleado_historia_laboral (
    id              BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    empresa_id      BIGINT NOT NULL,
    empleado_id     BIGINT NOT NULL,
    nombre_empresa  VARCHAR(191),
    cargo           VARCHAR(191),
    tipo_contrato   VARCHAR(40),
    fecha_inicio    DATE, fecha_final DATE,
    jefe_inmediato  TEXT,
    municipio_id    BIGINT,
    es_publico      BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, updated_at TIMESTAMP, deleted_at TIMESTAMP,
    CONSTRAINT fk_emp_hl_empresa   FOREIGN KEY (empresa_id)   REFERENCES empresa (id),
    CONSTRAINT fk_emp_hl_empleado  FOREIGN KEY (empleado_id)  REFERENCES tercero (id),
    CONSTRAINT fk_emp_hl_municipio FOREIGN KEY (municipio_id) REFERENCES municipio (id)
);
CREATE INDEX ix_emp_hl_empleado ON empleado_historia_laboral (empleado_id);

-- evaluacion_programa (← thu_programas_evaluaciones)
CREATE TABLE evaluacion_programa (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    empresa_id BIGINT NOT NULL,
    codigo VARCHAR(20), nombre VARCHAR(191) NOT NULL,
    fecha_inicial DATE, fecha_final DATE,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, updated_at TIMESTAMP, deleted_at TIMESTAMP,
    CONSTRAINT fk_eval_prog_empresa FOREIGN KEY (empresa_id) REFERENCES empresa (id)
);

-- evaluacion_desempeno (← thu_evaluaciones)
CREATE TABLE evaluacion_desempeno (
    id                     BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    empresa_id             BIGINT NOT NULL,
    evaluacion_programa_id BIGINT,
    empleado_id            BIGINT,                      -- evaluado (tercero)
    tipo_evaluacion        VARCHAR(40),
    fecha_inicial TIMESTAMP, fecha_final TIMESTAMP,
    calificacion           NUMERIC(7,2),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, updated_at TIMESTAMP, deleted_at TIMESTAMP,
    CONSTRAINT fk_eval_empresa  FOREIGN KEY (empresa_id)             REFERENCES empresa (id),
    CONSTRAINT fk_eval_programa FOREIGN KEY (evaluacion_programa_id) REFERENCES evaluacion_programa (id),
    CONSTRAINT fk_eval_empleado FOREIGN KEY (empleado_id)           REFERENCES tercero (id)
);
CREATE INDEX ix_eval_empleado ON evaluacion_desempeno (empleado_id);
