-- ============================================================================
-- V7 — Estructura organizacional de Común (centro_costo, dependencia, proyecto)
-- Nyxora · PostgreSQL · esquema public
--
-- Mapeado de com_centros_costos / com_dependencias / com_proyectos (subconjunto
-- comercial). Se POSPONE lo clínico (tipo_atencion, clase_servicio) y lo de nómina
-- (nom_vinculacion, nom_proceso). Depende de: V1 (empresa, sede), V5 (tercero).
-- ============================================================================

-- ----------------------------------------------------------------------------
-- centro_costo (← com_centros_costos) — jerárquico (nested set)
-- ----------------------------------------------------------------------------
CREATE TABLE centro_costo (
    id                     BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    empresa_id             BIGINT       NOT NULL,
    sede_id                BIGINT,                       -- ← com_sede_id
    centro_costo_padre_id  BIGINT,                       -- ← com_centro_costo_id
    codigo                 VARCHAR(20)  NOT NULL,
    nombre                 VARCHAR(150) NOT NULL,
    tipo_centro_costo      VARCHAR(30),                  -- ← tipo_centro_costo_id
    clase_centro_costo     VARCHAR(30),                  -- ← clase_centro_costo_id
    es_observacion         BOOLEAN      NOT NULL DEFAULT FALSE, -- ← es_observacion (nodo hoja imputable)
    maneja_plan_financiero BOOLEAN      NOT NULL DEFAULT FALSE, -- ← maneja_plan_financiero
    tercero_id             BIGINT,                       -- ← com_tercero_id (responsable)
    direccion              VARCHAR(500),                 -- ← direccion
    unidad_negocio_id      BIGINT,                       -- ← unidad_negocio_id (catálogo futuro)
    -- nested set
    izquierda              INT,
    derecha                INT,
    nivel                  INT,
    activo                 BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at             TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at             TIMESTAMP,
    deleted_at             TIMESTAMP,
    usuario_creacion       BIGINT,
    usuario_modificacion   BIGINT,
    CONSTRAINT fk_centro_costo_empresa FOREIGN KEY (empresa_id)            REFERENCES empresa (id),
    CONSTRAINT fk_centro_costo_sede    FOREIGN KEY (sede_id)               REFERENCES sede (id),
    CONSTRAINT fk_centro_costo_padre   FOREIGN KEY (centro_costo_padre_id) REFERENCES centro_costo (id),
    CONSTRAINT fk_centro_costo_tercero FOREIGN KEY (tercero_id)            REFERENCES tercero (id),
    CONSTRAINT uq_centro_costo_codigo  UNIQUE (empresa_id, codigo)
);
CREATE INDEX ix_centro_costo_empresa ON centro_costo (empresa_id);
CREATE INDEX ix_centro_costo_padre   ON centro_costo (centro_costo_padre_id);
COMMENT ON COLUMN centro_costo.es_observacion IS 'TRUE: nodo hoja imputable (recibe movimientos); FALSE: nodo agrupador.';

-- ----------------------------------------------------------------------------
-- dependencia (← com_dependencias) — jerárquica (nested set), bajo un centro de costo
-- ----------------------------------------------------------------------------
CREATE TABLE dependencia (
    id                     BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    empresa_id             BIGINT       NOT NULL,
    centro_costo_id        BIGINT,                       -- ← com_centro_costo_id
    dependencia_padre_id   BIGINT,                       -- ← com_dependencia_id
    codigo                 VARCHAR(40)  NOT NULL,
    nombre                 VARCHAR(191) NOT NULL,
    ubicacion              VARCHAR(500),                 -- ← ubicacion
    latitud                VARCHAR(40),                  -- ← latitud
    longitud               VARCHAR(40),                  -- ← longitud
    izquierda              INT,
    derecha                INT,
    nivel                  INT,
    activo                 BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at             TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at             TIMESTAMP,
    deleted_at             TIMESTAMP,
    usuario_creacion       BIGINT,
    usuario_modificacion   BIGINT,
    CONSTRAINT fk_dependencia_empresa      FOREIGN KEY (empresa_id)           REFERENCES empresa (id),
    CONSTRAINT fk_dependencia_centro_costo FOREIGN KEY (centro_costo_id)      REFERENCES centro_costo (id),
    CONSTRAINT fk_dependencia_padre        FOREIGN KEY (dependencia_padre_id) REFERENCES dependencia (id),
    CONSTRAINT uq_dependencia_codigo       UNIQUE (empresa_id, codigo)
);
CREATE INDEX ix_dependencia_empresa      ON dependencia (empresa_id);
CREATE INDEX ix_dependencia_centro_costo ON dependencia (centro_costo_id);

-- ----------------------------------------------------------------------------
-- proyecto (← com_proyectos)
-- ----------------------------------------------------------------------------
CREATE TABLE proyecto (
    id                     BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    empresa_id             BIGINT       NOT NULL,
    codigo                 VARCHAR(50)  NOT NULL,
    nombre                 VARCHAR(150) NOT NULL,
    descripcion            TEXT,
    programa_id            BIGINT,                       -- ← programa_id (catálogo futuro)
    fecha_inicio           DATE,
    fecha_final            DATE,
    activo                 BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at             TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at             TIMESTAMP,
    deleted_at             TIMESTAMP,
    usuario_creacion       BIGINT,
    usuario_modificacion   BIGINT,
    CONSTRAINT fk_proyecto_empresa FOREIGN KEY (empresa_id) REFERENCES empresa (id),
    CONSTRAINT uq_proyecto_codigo  UNIQUE (empresa_id, codigo)
);
CREATE INDEX ix_proyecto_empresa ON proyecto (empresa_id);
