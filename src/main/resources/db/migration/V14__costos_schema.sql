-- ============================================================================
-- V14 — Costos — mapeado de cos_recursos
-- Nyxora · PostgreSQL · esquema public · Depende de: V1 (empresa)
-- 'recurso' es referenciado por tercero/producto/movimiento_contable (recurso_id).
-- ============================================================================

CREATE TABLE recurso (
    id                   BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    empresa_id           BIGINT       NOT NULL,
    codigo               VARCHAR(10)  NOT NULL,
    nombre               VARCHAR(100) NOT NULL,
    tipo_recurso         VARCHAR(30),                  -- ← tipo_recurso_id
    driver               VARCHAR(30),                  -- ← driver_id (driver de costeo)
    costo_adicional      BOOLEAN      NOT NULL DEFAULT FALSE, -- ← costo_adicional
    descripcion          TEXT,
    activo               BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at           TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at           TIMESTAMP, deleted_at TIMESTAMP,
    usuario_creacion     BIGINT, usuario_modificacion BIGINT,
    CONSTRAINT fk_recurso_empresa FOREIGN KEY (empresa_id) REFERENCES empresa (id),
    CONSTRAINT uq_recurso_codigo  UNIQUE (empresa_id, codigo)
);
CREATE INDEX ix_recurso_empresa ON recurso (empresa_id);
