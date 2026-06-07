-- ============================================================================
-- V4 — Catálogos propios del módulo Común (reemplazan los *_id sin tabla limpia
-- del ERP de referencia y el EAV prv_listas_*). Reference data GLOBAL (sin empresa_id).
-- Nyxora · PostgreSQL · esquema public
-- ============================================================================

-- Tipo de identificación (CC, NIT, CE, PA, TI, RC, NUIP, NIE...)
CREATE TABLE tipo_identificacion (
    id        BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    codigo    VARCHAR(5)   NOT NULL,
    nombre    VARCHAR(100) NOT NULL,
    activo    BOOLEAN      NOT NULL DEFAULT TRUE,
    CONSTRAINT uq_tipo_identificacion_codigo UNIQUE (codigo)
);
COMMENT ON TABLE tipo_identificacion IS 'Tipo de documento de IDENTIDAD del tercero (distinto de tipo_documento transaccional).';

-- Género / estado civil (datos personales mínimos del tercero)
CREATE TABLE genero (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    codigo VARCHAR(5) NOT NULL, nombre VARCHAR(60) NOT NULL, activo BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT uq_genero_codigo UNIQUE (codigo)
);
CREATE TABLE estado_civil (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    codigo VARCHAR(5) NOT NULL, nombre VARCHAR(60) NOT NULL, activo BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT uq_estado_civil_codigo UNIQUE (codigo)
);

-- Clasificación de tercero (cliente, proveedor, empleado, acreedor, etc.)
CREATE TABLE tipo_tercero (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    codigo VARCHAR(20) NOT NULL, nombre VARCHAR(100) NOT NULL, activo BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT uq_tipo_tercero_codigo UNIQUE (codigo)
);

-- Tributario (DIAN)
CREATE TABLE tipo_contribuyente (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    codigo VARCHAR(20) NOT NULL, nombre VARCHAR(100) NOT NULL, activo BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT uq_tipo_contribuyente_codigo UNIQUE (codigo)
);
CREATE TABLE actividad_economica (   -- CIIU
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    codigo VARCHAR(10) NOT NULL, nombre VARCHAR(255) NOT NULL, activo BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT uq_actividad_economica_codigo UNIQUE (codigo)
);

-- Pagos
CREATE TABLE condicion_pago (   -- contado, crédito 30, crédito 60...
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    codigo VARCHAR(20) NOT NULL, nombre VARCHAR(100) NOT NULL,
    dias  INT NOT NULL DEFAULT 0, activo BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT uq_condicion_pago_codigo UNIQUE (codigo)
);
CREATE TABLE forma_pago (   -- efectivo, transferencia, cheque, tarjeta...
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    codigo VARCHAR(20) NOT NULL, nombre VARCHAR(100) NOT NULL, activo BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT uq_forma_pago_codigo UNIQUE (codigo)
);

-- Bancos y tipos de cuenta
CREATE TABLE banco (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    codigo VARCHAR(20) NOT NULL, nombre VARCHAR(150) NOT NULL, activo BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT uq_banco_codigo UNIQUE (codigo)
);
CREATE TABLE tipo_cuenta_bancaria (   -- ahorros, corriente
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    codigo VARCHAR(20) NOT NULL, nombre VARCHAR(60) NOT NULL, activo BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT uq_tipo_cuenta_bancaria_codigo UNIQUE (codigo)
);

-- Geografía DANE (país → departamento → municipio → barrio)
CREATE TABLE pais (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    codigo VARCHAR(5) NOT NULL, nombre VARCHAR(100) NOT NULL, activo BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT uq_pais_codigo UNIQUE (codigo)
);
CREATE TABLE departamento (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    pais_id BIGINT NOT NULL,
    codigo VARCHAR(5) NOT NULL, nombre VARCHAR(120) NOT NULL, activo BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT fk_departamento_pais FOREIGN KEY (pais_id) REFERENCES pais (id),
    CONSTRAINT uq_departamento_codigo UNIQUE (pais_id, codigo)
);
CREATE TABLE municipio (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    departamento_id BIGINT NOT NULL,
    codigo VARCHAR(5) NOT NULL, nombre VARCHAR(120) NOT NULL, activo BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT fk_municipio_departamento FOREIGN KEY (departamento_id) REFERENCES departamento (id),
    CONSTRAINT uq_municipio_codigo UNIQUE (departamento_id, codigo)
);
CREATE TABLE barrio (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    municipio_id BIGINT NOT NULL,
    codigo VARCHAR(10), nombre VARCHAR(150) NOT NULL, activo BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT fk_barrio_municipio FOREIGN KEY (municipio_id) REFERENCES municipio (id)
);
CREATE INDEX ix_municipio_departamento ON municipio (departamento_id);
CREATE INDEX ix_barrio_municipio       ON barrio (municipio_id);
