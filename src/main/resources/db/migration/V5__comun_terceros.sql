-- ============================================================================
-- V5 — Maestro de TERCEROS (módulo Común) — diseño rico mapeado del com_terceros real
-- Nyxora · PostgreSQL · esquema public
--
-- Mapeo desde public.com_terceros (~100 col, tabla-Dios) → diseño limpio:
--   • Se CONSERVA el subconjunto comercial/fiscal (ver docs/base-datos/mapeo-referencia-nyxora.md §4.1).
--   • Se POSPONE lo clínico/RRHH al vertical salud (§4.2).
--   • *_id → FK a catálogos propios (V4). smallint(0/1) → BOOLEAN. json de listas → satélites.
-- Depende de: V1 (empresa), V4 (catálogos).
-- ============================================================================

CREATE TABLE tercero (
    id                        BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    empresa_id                BIGINT       NOT NULL,
    -- Identificación
    tipo_identificacion_id    BIGINT       NOT NULL,          -- ← tipo_documento_id
    numero_documento          VARCHAR(40)  NOT NULL,          -- ← numero_documento
    digito_verificacion       SMALLINT,                       -- ← digito_verificacion (DV NIT)
    tipo_persona              VARCHAR(10)  NOT NULL,           -- ← es_empresa (0/1) → natural|juridica
    codigo                    VARCHAR(20),                    -- ← codigo (código interno)
    -- Persona natural
    primer_nombre             VARCHAR(150),                   -- ← nombre1
    segundo_nombre            VARCHAR(150),                   -- ← nombre2
    primer_apellido           VARCHAR(150),                   -- ← apellido1
    segundo_apellido          VARCHAR(150),                   -- ← apellido2
    -- Persona jurídica
    razon_social              VARCHAR(255),                   -- ← razon_social
    nombre_comercial          VARCHAR(150),                   -- ← razon_comercial
    nombre_representante_legal     VARCHAR(150),              -- ← nombre_representante_legal
    documento_representante_legal  VARCHAR(40),               -- ← documento_representante_legal
    -- Nombre normalizado para mostrar/buscar
    nombre                    VARCHAR(255) NOT NULL,
    -- Personal mínimo
    genero_id                 BIGINT,                         -- ← genero_id
    estado_civil_id           BIGINT,                         -- ← estado_civil_id
    fecha_nacimiento          DATE,                           -- ← fecha_nacimiento
    -- Ubicación principal
    municipio_id              BIGINT,                         -- ← municipio_residencia_id
    barrio_id                 BIGINT,                         -- ← barrio_residencia_id
    direccion                 VARCHAR(500),                   -- ← direccion_residencia
    sitio_web                 VARCHAR(80),                    -- ← sitio_web
    -- Documento de identidad (expedición/vencimiento)
    fecha_expedicion_documento     DATE,                      -- ← fecha_expedicion_documento
    municipio_expedicion_id        BIGINT,                    -- ← municipio_expedicion_documento_id
    fecha_vencimiento_documento    DATE,                      -- ← fecha_vencimiento_documento
    -- Fiscal / DIAN
    actividad_economica_id    BIGINT,                         -- ← actividad_economica_id (CIIU)
    tipo_contribuyente_id     BIGINT,                         -- ← tipo_contribuyente_id
    responsable_iva           BOOLEAN      NOT NULL DEFAULT FALSE, -- ← responsable_iva
    es_autoretenedor_iva      BOOLEAN      NOT NULL DEFAULT FALSE, -- ← es_autoretenedor_iva
    es_autoretenedor_ica      BOOLEAN      NOT NULL DEFAULT FALSE, -- ← es_autoretenedor_ica
    es_autoretenedor_fuente   BOOLEAN      NOT NULL DEFAULT FALSE, -- ← es_autoretenedor_fuente
    declarante                BOOLEAN      NOT NULL DEFAULT FALSE, -- ← declarante
    aplica_art_383            BOOLEAN      NOT NULL DEFAULT FALSE, -- ← aplica_art_383
    tiene_rut                 BOOLEAN      NOT NULL DEFAULT FALSE, -- ← rut (0/1)
    obligacion_dian           JSONB,                          -- ← obligacion_dian (json)
    -- Comercial
    condicion_pago_cliente_id     BIGINT,                     -- ← condicion_pago_cliente_id
    condicion_pago_proveedor_id   BIGINT,                     -- ← condicion_pago_proveedor_id
    forma_pago_cliente_id         BIGINT,                     -- ← forma_pago_cliente_id
    forma_pago_proveedor_id       BIGINT,                     -- ← forma_pago_proveedor_id
    interes_efectivo_mensual      NUMERIC(7,4),               -- ← interes_efectivo_mensual
    cuenta_contable_proveedor_id  BIGINT,                     -- ← con_plan_contable_proveedor_id (FK a cuenta cuando exista Contabilidad)
    recurso_id                    BIGINT,                     -- ← cos_recurso_id (FK a recurso de costos, módulo futuro)
    es_reciproco              BOOLEAN      NOT NULL DEFAULT FALSE, -- ← es_reciproco
    codigo_reciproco          VARCHAR(150),                   -- ← codigo_reciproco
    -- Otros
    metadatos                 JSONB,                          -- ← metadatos (json)
    observaciones             VARCHAR(500),
    activo                    BOOLEAN      NOT NULL DEFAULT TRUE,   -- ← activo (0/1)
    created_at                TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at                TIMESTAMP,
    deleted_at                TIMESTAMP,
    usuario_creacion          BIGINT,
    usuario_modificacion      BIGINT,
    CONSTRAINT fk_tercero_empresa             FOREIGN KEY (empresa_id)              REFERENCES empresa (id),
    CONSTRAINT fk_tercero_tipo_identificacion FOREIGN KEY (tipo_identificacion_id)  REFERENCES tipo_identificacion (id),
    CONSTRAINT fk_tercero_genero              FOREIGN KEY (genero_id)               REFERENCES genero (id),
    CONSTRAINT fk_tercero_estado_civil        FOREIGN KEY (estado_civil_id)         REFERENCES estado_civil (id),
    CONSTRAINT fk_tercero_municipio           FOREIGN KEY (municipio_id)            REFERENCES municipio (id),
    CONSTRAINT fk_tercero_barrio              FOREIGN KEY (barrio_id)               REFERENCES barrio (id),
    CONSTRAINT fk_tercero_municipio_exp       FOREIGN KEY (municipio_expedicion_id) REFERENCES municipio (id),
    CONSTRAINT fk_tercero_actividad           FOREIGN KEY (actividad_economica_id)  REFERENCES actividad_economica (id),
    CONSTRAINT fk_tercero_tipo_contribuyente  FOREIGN KEY (tipo_contribuyente_id)   REFERENCES tipo_contribuyente (id),
    CONSTRAINT fk_tercero_cond_pago_cli       FOREIGN KEY (condicion_pago_cliente_id)   REFERENCES condicion_pago (id),
    CONSTRAINT fk_tercero_cond_pago_prov      FOREIGN KEY (condicion_pago_proveedor_id) REFERENCES condicion_pago (id),
    CONSTRAINT fk_tercero_forma_pago_cli      FOREIGN KEY (forma_pago_cliente_id)   REFERENCES forma_pago (id),
    CONSTRAINT fk_tercero_forma_pago_prov     FOREIGN KEY (forma_pago_proveedor_id) REFERENCES forma_pago (id),
    CONSTRAINT uq_tercero_identificacion      UNIQUE (empresa_id, tipo_identificacion_id, numero_documento),
    CONSTRAINT ck_tercero_tipo_persona        CHECK (tipo_persona IN ('natural', 'juridica'))
);
CREATE INDEX ix_tercero_empresa        ON tercero (empresa_id);
CREATE INDEX ix_tercero_documento      ON tercero (empresa_id, numero_documento);
CREATE INDEX ix_tercero_nombre         ON tercero (empresa_id, nombre);
COMMENT ON TABLE  tercero IS 'Maestro único de terceros (subconjunto comercial/fiscal del com_terceros real). Lo clínico/RRHH va al vertical salud.';
COMMENT ON COLUMN tercero.nombre IS 'Nombre normalizado (razón social o nombres+apellidos) para mostrar y buscar.';

-- ----------------------------------------------------------------------------
-- Clasificación del tercero (← com_terceros_por_tipos): cliente/proveedor/empleado/...
-- ----------------------------------------------------------------------------
CREATE TABLE tercero_clasificacion (
    id              BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    tercero_id      BIGINT  NOT NULL,
    tipo_tercero_id BIGINT  NOT NULL,
    activo          BOOLEAN NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at      TIMESTAMP,
    CONSTRAINT fk_tercero_clasif_tercero FOREIGN KEY (tercero_id)      REFERENCES tercero (id),
    CONSTRAINT fk_tercero_clasif_tipo    FOREIGN KEY (tipo_tercero_id) REFERENCES tipo_tercero (id),
    CONSTRAINT uq_tercero_clasif         UNIQUE (tercero_id, tipo_tercero_id)
);

-- ----------------------------------------------------------------------------
-- Contactos (← com_terceros_contactos)
-- ----------------------------------------------------------------------------
CREATE TABLE tercero_contacto (
    id            BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    tercero_id    BIGINT       NOT NULL,
    nombre        VARCHAR(150) NOT NULL,
    cargo         VARCHAR(150),
    telefono      VARCHAR(30),
    celular       VARCHAR(30),
    email         VARCHAR(255),
    notas         VARCHAR(500),
    principal     BOOLEAN      NOT NULL DEFAULT FALSE,
    activo        BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP,
    deleted_at    TIMESTAMP,
    CONSTRAINT fk_tercero_contacto_tercero FOREIGN KEY (tercero_id) REFERENCES tercero (id)
);
CREATE INDEX ix_tercero_contacto_tercero ON tercero_contacto (tercero_id);

-- ----------------------------------------------------------------------------
-- Direcciones adicionales (← otras_direcciones json)
-- ----------------------------------------------------------------------------
CREATE TABLE tercero_direccion (
    id            BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    tercero_id    BIGINT       NOT NULL,
    tipo          VARCHAR(20)  NOT NULL DEFAULT 'principal',  -- principal | facturacion | envio
    direccion     VARCHAR(500) NOT NULL,
    municipio_id  BIGINT,
    barrio_id     BIGINT,
    codigo_postal VARCHAR(10),
    telefono      VARCHAR(30),
    principal     BOOLEAN      NOT NULL DEFAULT FALSE,
    activo        BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP,
    deleted_at    TIMESTAMP,
    CONSTRAINT fk_tercero_dir_tercero   FOREIGN KEY (tercero_id)   REFERENCES tercero (id),
    CONSTRAINT fk_tercero_dir_municipio FOREIGN KEY (municipio_id) REFERENCES municipio (id),
    CONSTRAINT fk_tercero_dir_barrio    FOREIGN KEY (barrio_id)    REFERENCES barrio (id),
    CONSTRAINT ck_tercero_dir_tipo      CHECK (tipo IN ('principal', 'facturacion', 'envio'))
);
CREATE INDEX ix_tercero_direccion_tercero ON tercero_direccion (tercero_id);

-- ----------------------------------------------------------------------------
-- Cuentas bancarias (← banco_1/2 + tipo_cuenta_1/2 + numero_cuenta_1/2 inline → normalizado)
-- ----------------------------------------------------------------------------
CREATE TABLE tercero_cuenta_bancaria (
    id                      BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    tercero_id              BIGINT      NOT NULL,
    banco_id                BIGINT      NOT NULL,
    tipo_cuenta_bancaria_id BIGINT      NOT NULL,
    numero_cuenta           VARCHAR(50) NOT NULL,
    principal               BOOLEAN     NOT NULL DEFAULT FALSE,
    activo                  BOOLEAN     NOT NULL DEFAULT TRUE,
    created_at              TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at              TIMESTAMP,
    deleted_at              TIMESTAMP,
    CONSTRAINT fk_tercero_cta_tercero FOREIGN KEY (tercero_id)              REFERENCES tercero (id),
    CONSTRAINT fk_tercero_cta_banco   FOREIGN KEY (banco_id)                REFERENCES banco (id),
    CONSTRAINT fk_tercero_cta_tipo    FOREIGN KEY (tipo_cuenta_bancaria_id) REFERENCES tipo_cuenta_bancaria (id)
);
CREATE INDEX ix_tercero_cuenta_tercero ON tercero_cuenta_bancaria (tercero_id);
