-- ============================================================================
-- V1 — Núcleo / plataforma (módulo Administración)
-- ERP MVP · PostgreSQL · Fase 11 (alineado a convención backend v3)
--
-- Convenciones v3 aplicadas:
--   • Esquema 'public' plano (sin prefijo por módulo).
--   • Tablas y columnas en español snake_case.
--   • Multi-tenant por columna empresa_id (FK a empresa).
--   • Auditoría: created_at / updated_at / deleted_at (inglés) +
--     usuario_creacion / usuario_modificacion (español) + activo Boolean.
--   • Soft-delete (deleted_at); nunca DELETE físico.
--   • Estados como VARCHAR + CHECK (no enum nativo).
-- ============================================================================

-- ----------------------------------------------------------------------------
-- empresa (tenant raíz; NO lleva empresa_id, es la raíz de la multitenencia)
-- ----------------------------------------------------------------------------
CREATE TABLE empresa (
    id                   BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nit                  VARCHAR(20)  NOT NULL,
    razon_social         VARCHAR(255) NOT NULL,
    tipo_persona         VARCHAR(10)  NOT NULL,
    activo               BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at           TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at           TIMESTAMP,
    deleted_at           TIMESTAMP,
    usuario_creacion     BIGINT,
    usuario_modificacion BIGINT,
    CONSTRAINT uq_empresa_nit  UNIQUE (nit),
    CONSTRAINT ck_empresa_tipo CHECK (tipo_persona IN ('natural', 'juridica'))
);
COMMENT ON TABLE  empresa IS 'Tenant raíz del ERP (multiempresa).';
COMMENT ON COLUMN empresa.nit IS 'Identificación tributaria, única en el sistema.';

-- ----------------------------------------------------------------------------
-- sede (multisede)
-- ----------------------------------------------------------------------------
CREATE TABLE sede (
    id                   BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    empresa_id           BIGINT       NOT NULL,
    codigo               VARCHAR(20)  NOT NULL,
    nombre               VARCHAR(255) NOT NULL,
    activo               BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at           TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at           TIMESTAMP,
    deleted_at           TIMESTAMP,
    usuario_creacion     BIGINT,
    usuario_modificacion BIGINT,
    CONSTRAINT fk_sede_empresa FOREIGN KEY (empresa_id) REFERENCES empresa (id),
    CONSTRAINT uq_sede_codigo  UNIQUE (empresa_id, codigo)
);
CREATE INDEX ix_sede_empresa ON sede (empresa_id);

-- ----------------------------------------------------------------------------
-- usuario
-- ----------------------------------------------------------------------------
CREATE TABLE usuario (
    id                   BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    empresa_id           BIGINT       NOT NULL,
    username             VARCHAR(60)  NOT NULL,
    email                VARCHAR(255) NOT NULL,
    hash_password        VARCHAR(255) NOT NULL,
    activo               BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at           TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at           TIMESTAMP,
    deleted_at           TIMESTAMP,
    usuario_creacion     BIGINT,
    usuario_modificacion BIGINT,
    CONSTRAINT fk_usuario_empresa FOREIGN KEY (empresa_id) REFERENCES empresa (id),
    CONSTRAINT uq_usuario_username UNIQUE (empresa_id, username)
);
CREATE INDEX ix_usuario_empresa ON usuario (empresa_id);
COMMENT ON COLUMN usuario.hash_password IS 'Hash (bcrypt/argon2); nunca texto plano.';

-- ----------------------------------------------------------------------------
-- rol / permiso / asociaciones (RBAC)
-- ----------------------------------------------------------------------------
CREATE TABLE rol (
    id                   BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    empresa_id           BIGINT       NOT NULL,
    nombre               VARCHAR(100) NOT NULL,
    activo               BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at           TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at           TIMESTAMP,
    deleted_at           TIMESTAMP,
    usuario_creacion     BIGINT,
    usuario_modificacion BIGINT,
    CONSTRAINT fk_rol_empresa FOREIGN KEY (empresa_id) REFERENCES empresa (id),
    CONSTRAINT uq_rol_nombre  UNIQUE (empresa_id, nombre)
);
CREATE INDEX ix_rol_empresa ON rol (empresa_id);

-- permiso: catálogo GLOBAL de referencia (sin empresa_id por diseño)
CREATE TABLE permiso (
    id          BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    codigo      VARCHAR(100) NOT NULL,
    descripcion VARCHAR(255) NOT NULL,
    CONSTRAINT uq_permiso_codigo UNIQUE (codigo)
);
COMMENT ON TABLE permiso IS 'Catálogo global de permisos (p. ej. compras.orden.aprobar). No es multi-tenant.';

-- tablas de asociación pura (sin auditoría)
CREATE TABLE rol_permiso (
    rol_id     BIGINT NOT NULL,
    permiso_id BIGINT NOT NULL,
    CONSTRAINT pk_rol_permiso PRIMARY KEY (rol_id, permiso_id),
    CONSTRAINT fk_rol_permiso_rol     FOREIGN KEY (rol_id)     REFERENCES rol (id),
    CONSTRAINT fk_rol_permiso_permiso FOREIGN KEY (permiso_id) REFERENCES permiso (id)
);

CREATE TABLE usuario_rol (
    usuario_id BIGINT NOT NULL,
    rol_id     BIGINT NOT NULL,
    sede_id    BIGINT NOT NULL,
    CONSTRAINT pk_usuario_rol PRIMARY KEY (usuario_id, rol_id, sede_id),
    CONSTRAINT fk_usuario_rol_usuario FOREIGN KEY (usuario_id) REFERENCES usuario (id),
    CONSTRAINT fk_usuario_rol_rol     FOREIGN KEY (rol_id)     REFERENCES rol (id),
    CONSTRAINT fk_usuario_rol_sede    FOREIGN KEY (sede_id)    REFERENCES sede (id)
);
COMMENT ON TABLE usuario_rol IS 'Habilita a un usuario con un rol en una sede concreta.';

-- ----------------------------------------------------------------------------
-- vigencia (periodo fiscal) — máquina de estados
-- ----------------------------------------------------------------------------
CREATE TABLE vigencia (
    id                   BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    empresa_id           BIGINT      NOT NULL,
    anio                 INT         NOT NULL,
    estado               VARCHAR(15) NOT NULL DEFAULT 'planeada',
    fecha_apertura       DATE,
    fecha_cierre         DATE,
    activo               BOOLEAN     NOT NULL DEFAULT TRUE,
    created_at           TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at           TIMESTAMP,
    deleted_at           TIMESTAMP,
    usuario_creacion     BIGINT,
    usuario_modificacion BIGINT,
    CONSTRAINT fk_vigencia_empresa FOREIGN KEY (empresa_id) REFERENCES empresa (id),
    CONSTRAINT uq_vigencia_anio    UNIQUE (empresa_id, anio),
    CONSTRAINT ck_vigencia_estado  CHECK (estado IN ('planeada', 'abierta', 'en_cierre', 'cerrada'))
);
CREATE INDEX ix_vigencia_empresa ON vigencia (empresa_id);
COMMENT ON COLUMN vigencia.estado IS 'planeada -> abierta -> en_cierre -> cerrada (transiciones validadas en la app).';

-- ----------------------------------------------------------------------------
-- parametro
-- ----------------------------------------------------------------------------
CREATE TABLE parametro (
    id                   BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    empresa_id           BIGINT       NOT NULL,
    clave                VARCHAR(100) NOT NULL,
    valor                VARCHAR(500) NOT NULL,
    tipo_dato            VARCHAR(20)  NOT NULL DEFAULT 'string',
    activo               BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at           TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at           TIMESTAMP,
    deleted_at           TIMESTAMP,
    usuario_creacion     BIGINT,
    usuario_modificacion BIGINT,
    CONSTRAINT fk_parametro_empresa FOREIGN KEY (empresa_id) REFERENCES empresa (id),
    CONSTRAINT uq_parametro_clave   UNIQUE (empresa_id, clave),
    CONSTRAINT ck_parametro_tipo    CHECK (tipo_dato IN ('string', 'int', 'decimal', 'bool', 'date'))
);
CREATE INDEX ix_parametro_empresa ON parametro (empresa_id);

-- ----------------------------------------------------------------------------
-- auditoria (universal, append-only: sin updated_at/deleted_at)
-- ----------------------------------------------------------------------------
CREATE TABLE auditoria (
    id               BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    empresa_id       BIGINT       NOT NULL,
    usuario_id       BIGINT,
    entidad          VARCHAR(100) NOT NULL,
    entidad_id       BIGINT,
    accion           VARCHAR(20)  NOT NULL,
    valores_antes    JSONB,
    valores_despues  JSONB,
    created_at       TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT ck_auditoria_accion CHECK (accion IN ('crear', 'actualizar', 'eliminar', 'anular'))
);
CREATE INDEX ix_auditoria_entidad     ON auditoria (entidad, entidad_id);
CREATE INDEX ix_auditoria_empresa_fec ON auditoria (empresa_id, created_at);
COMMENT ON TABLE auditoria IS 'Bitácora universal de cambios. Append-only.';
