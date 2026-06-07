-- ============================================================================
-- V25 — Soporte transversal: adjuntos (← com_adjuntos / com_objetos_adjuntos)
-- Nyxora · PostgreSQL · esquema public
--
-- Adjunto polimórfico: cualquier entidad de cualquier módulo puede tener archivos,
-- referenciados por (modulo, entidad, entidad_id) — sin FK cruzada. Reemplaza el patrón
-- com_objetos_adjuntos del real. Depende de: V1 (empresa).
-- ============================================================================

CREATE TABLE adjunto (
    id               BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    empresa_id       BIGINT       NOT NULL,
    modulo           VARCHAR(40)  NOT NULL,           -- p.ej. compras, facturacion, tercero...
    entidad          VARCHAR(60)  NOT NULL,           -- nombre de la entidad (tabla lógica)
    entidad_id       BIGINT       NOT NULL,           -- id del registro dueño
    nombre           VARCHAR(255) NOT NULL,
    tipo_mime        VARCHAR(100),
    url              VARCHAR(1000) NOT NULL,          -- ruta/URL del almacenamiento (S3/disco)
    tamano_bytes     BIGINT,
    descripcion      VARCHAR(500),
    activo           BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at       TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at       TIMESTAMP,
    deleted_at       TIMESTAMP,
    usuario_creacion BIGINT,
    CONSTRAINT fk_adjunto_empresa FOREIGN KEY (empresa_id) REFERENCES empresa (id)
);
CREATE INDEX ix_adjunto_empresa ON adjunto (empresa_id);
CREATE INDEX ix_adjunto_objeto  ON adjunto (modulo, entidad, entidad_id);
COMMENT ON TABLE adjunto IS 'Archivos adjuntos polimórficos por (modulo, entidad, entidad_id). El binario vive en almacenamiento externo; aquí solo la referencia.';
