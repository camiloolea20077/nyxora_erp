-- ============================================================================
-- V29 — La cuenta bancaria PROPIA referencia el banco como un TERCERO (rol BANCO),
-- no el catálogo 'banco'. Se repunta el FK de cuenta_bancaria.banco_id a tercero(id).
--
-- Se usa NOT VALID para no fallar el arranque si existieran filas previas que apunten
-- al catálogo 'banco' (no se validan las existentes, pero sí toda alta/edición futura).
-- Depende de: V5 (tercero), V16 (cuenta_bancaria).
-- ============================================================================

ALTER TABLE cuenta_bancaria DROP CONSTRAINT IF EXISTS fk_ctabanco_banco;

ALTER TABLE cuenta_bancaria
    ADD CONSTRAINT fk_ctabanco_banco_tercero FOREIGN KEY (banco_id) REFERENCES tercero (id) NOT VALID;

COMMENT ON COLUMN cuenta_bancaria.banco_id IS
    'Tercero con rol BANCO (tipo_tercero BANCO). FK a tercero(id).';
