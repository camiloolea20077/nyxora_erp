-- ============================================================================
-- V23 — Jurídico / Procesos disciplinarios — mapeado de jur_*
-- Depende de: V1, V5 (tercero), V21 (vinculacion).
-- ============================================================================

CREATE TABLE clasificacion_falta (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    empresa_id BIGINT NOT NULL,
    codigo VARCHAR(20) NOT NULL, nombre VARCHAR(150) NOT NULL, activo BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, deleted_at TIMESTAMP,
    CONSTRAINT fk_clasif_falta_empresa FOREIGN KEY (empresa_id) REFERENCES empresa (id),
    CONSTRAINT uq_clasif_falta_codigo UNIQUE (empresa_id, codigo)
);

CREATE TABLE falta (
    id              BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    empresa_id      BIGINT NOT NULL,
    clasificacion_falta_id BIGINT,
    codigo          VARCHAR(20) NOT NULL,
    nombre          VARCHAR(150) NOT NULL,
    descripcion     TEXT,
    caducidad_dias  INT,
    politica        TEXT,
    activo          BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, updated_at TIMESTAMP, deleted_at TIMESTAMP,
    CONSTRAINT fk_falta_empresa FOREIGN KEY (empresa_id) REFERENCES empresa (id),
    CONSTRAINT fk_falta_clasif  FOREIGN KEY (clasificacion_falta_id) REFERENCES clasificacion_falta (id),
    CONSTRAINT uq_falta_codigo  UNIQUE (empresa_id, codigo)
);

-- proceso_disciplinario (← jur_encabezados_procesos)
CREATE TABLE proceso_disciplinario (
    id              BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    empresa_id      BIGINT NOT NULL,
    fecha           DATE   NOT NULL,
    vinculacion_id  BIGINT,                            -- ← nom_vinculacion_id (investigado)
    responsable_id  BIGINT,                            -- ← com_tercero_responsable_id
    descripcion     TEXT,
    estado          VARCHAR(30),                       -- ← prv_lista_elemento_estado_id
    activo          BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, updated_at TIMESTAMP, deleted_at TIMESTAMP,
    usuario_creacion BIGINT, usuario_modificacion BIGINT,
    CONSTRAINT fk_proc_empresa     FOREIGN KEY (empresa_id)     REFERENCES empresa (id),
    CONSTRAINT fk_proc_vinculacion FOREIGN KEY (vinculacion_id) REFERENCES vinculacion (id),
    CONSTRAINT fk_proc_responsable FOREIGN KEY (responsable_id) REFERENCES tercero (id)
);
CREATE INDEX ix_proc_empresa ON proceso_disciplinario (empresa_id);

-- proceso_falta (← jur_detalles_procesos_faltas)
CREATE TABLE proceso_falta (
    id                       BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    proceso_disciplinario_id BIGINT NOT NULL,
    falta_id                 BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, deleted_at TIMESTAMP,
    CONSTRAINT fk_proc_falta_proceso FOREIGN KEY (proceso_disciplinario_id) REFERENCES proceso_disciplinario (id),
    CONSTRAINT fk_proc_falta_falta   FOREIGN KEY (falta_id)                 REFERENCES falta (id)
);
CREATE INDEX ix_proc_falta_proceso ON proceso_falta (proceso_disciplinario_id);

-- proceso_descargo (← jur_detalles_procesos_descargos)
CREATE TABLE proceso_descargo (
    id                       BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    proceso_disciplinario_id BIGINT NOT NULL,
    fecha                    DATE,
    texto                    TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, deleted_at TIMESTAMP,
    CONSTRAINT fk_descargo_proceso FOREIGN KEY (proceso_disciplinario_id) REFERENCES proceso_disciplinario (id)
);
CREATE INDEX ix_descargo_proceso ON proceso_descargo (proceso_disciplinario_id);

-- proceso_notificacion (← jur_detalle_procesos_notificaciones)
CREATE TABLE proceso_notificacion (
    id                       BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    proceso_disciplinario_id BIGINT NOT NULL,
    fecha                    DATE,
    tipo                     VARCHAR(40),
    texto                    TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, deleted_at TIMESTAMP,
    CONSTRAINT fk_notif_proceso FOREIGN KEY (proceso_disciplinario_id) REFERENCES proceso_disciplinario (id)
);
CREATE INDEX ix_notif_proceso ON proceso_notificacion (proceso_disciplinario_id);
