-- ============================================================================
-- V27 — Enriquecer 'empresa' (tenant raíz) con identidad fiscal, dirección y contacto.
-- Nyxora · PostgreSQL
--
-- En el ERP de referencia estos datos vivían en un tercero (prv_empresas.com_tercero_id);
-- aquí se denormalizan en 'empresa' (es la raíz del tenant). La config de Facturación
-- Electrónica (software_id_fe, pin_fe...) se modelará con el módulo Facturación.
-- Columnas nullable (no rompen la fila demo existente).
-- ============================================================================

ALTER TABLE empresa
    ADD COLUMN codigo                 VARCHAR(25),
    ADD COLUMN digito_verificacion    SMALLINT,
    ADD COLUMN nombre_comercial       VARCHAR(250),
    ADD COLUMN representante_legal     VARCHAR(255),
    ADD COLUMN regimen_tributario     VARCHAR(40),
    ADD COLUMN tipo_contribuyente_id  BIGINT,
    ADD COLUMN responsabilidad_fiscal VARCHAR(120),
    ADD COLUMN actividad_economica_id BIGINT,
    ADD COLUMN sector                 VARCHAR(40),
    ADD COLUMN email                  VARCHAR(255),
    ADD COLUMN telefono               VARCHAR(30),
    ADD COLUMN celular                VARCHAR(30),
    ADD COLUMN sitio_web              VARCHAR(120),
    ADD COLUMN municipio_id           BIGINT,
    ADD COLUMN direccion              VARCHAR(500),
    ADD COLUMN codigo_postal          VARCHAR(10),
    ADD COLUMN logo_url               VARCHAR(1000);

ALTER TABLE empresa ADD CONSTRAINT fk_empresa_tipo_contribuyente
    FOREIGN KEY (tipo_contribuyente_id) REFERENCES tipo_contribuyente (id);
ALTER TABLE empresa ADD CONSTRAINT fk_empresa_actividad
    FOREIGN KEY (actividad_economica_id) REFERENCES actividad_economica (id);
ALTER TABLE empresa ADD CONSTRAINT fk_empresa_municipio
    FOREIGN KEY (municipio_id) REFERENCES municipio (id);

COMMENT ON COLUMN empresa.digito_verificacion IS 'Dígito de verificación del NIT.';
