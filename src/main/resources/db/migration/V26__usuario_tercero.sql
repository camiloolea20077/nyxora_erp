-- ============================================================================
-- V26 — El usuario (cuenta de acceso) pertenece a un tercero (la persona).
-- Nyxora · PostgreSQL
--
-- Nullable a nivel BD (para no romper filas existentes), pero OBLIGATORIO por regla de
-- negocio en el service. Índice único parcial: un usuario ACTIVO por tercero.
-- ============================================================================

ALTER TABLE usuario ADD COLUMN tercero_id BIGINT;

ALTER TABLE usuario ADD CONSTRAINT fk_usuario_tercero
    FOREIGN KEY (tercero_id) REFERENCES tercero (id);

CREATE INDEX ix_usuario_tercero ON usuario (tercero_id);

-- Un único usuario activo por tercero (ignora nulos y soft-deleted)
CREATE UNIQUE INDEX uq_usuario_tercero_activo
    ON usuario (tercero_id)
    WHERE deleted_at IS NULL AND tercero_id IS NOT NULL;

COMMENT ON COLUMN usuario.tercero_id IS 'Tercero (persona/entidad) dueño de esta cuenta de acceso. Obligatorio por negocio.';
