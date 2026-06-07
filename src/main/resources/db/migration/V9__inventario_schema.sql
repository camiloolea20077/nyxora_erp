-- ============================================================================
-- V9 — Inventario — mapeado de inv_bodegas / inv_ubicaciones / inv_lotes /
--      inv_detalles_inventarios / inv_productos_saldos
-- Nyxora · PostgreSQL · esquema public
--
-- Va ANTES de Compras porque la orden/recepción referencian 'bodega'.
-- Mejoras: movimiento_inventario APPEND-ONLY con tipo explícito; saldo como PROYECCIÓN.
-- Se POSPONE lo de activos fijos (acf_*), AIU y SST presentes en el detalle universal real.
-- Depende de: V1 (empresa, sede), V2 (unidad_medida, impuesto), V5 (tercero),
--             V6 (producto, producto_variante), V7 (centro_costo).
-- ============================================================================

-- marca (← inv_marcas)
CREATE TABLE marca (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    empresa_id BIGINT NOT NULL,
    codigo VARCHAR(20) NOT NULL,
    nombre VARCHAR(150) NOT NULL,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP, deleted_at TIMESTAMP,
    CONSTRAINT fk_marca_empresa FOREIGN KEY (empresa_id) REFERENCES empresa (id),
    CONSTRAINT uq_marca_codigo UNIQUE (empresa_id, codigo)
);

-- bodega (← inv_bodegas)
CREATE TABLE bodega (
    id                    BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    empresa_id            BIGINT       NOT NULL,
    sede_id               BIGINT,                       -- ← com_sede_id
    centro_costo_id       BIGINT,                       -- ← com_centro_costo_id
    codigo                VARCHAR(20)  NOT NULL,
    nombre                VARCHAR(150) NOT NULL,
    tipo_abastecimiento   VARCHAR(30),                  -- ← tipo_abastecimiento_id
    direccion             VARCHAR(500),
    latitud               VARCHAR(20),
    longitud              VARCHAR(20),
    permite_compra        BOOLEAN      NOT NULL DEFAULT TRUE, -- ← permite_compra
    activo                BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at            TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at            TIMESTAMP, deleted_at TIMESTAMP,
    usuario_creacion      BIGINT, usuario_modificacion BIGINT,
    CONSTRAINT fk_bodega_empresa      FOREIGN KEY (empresa_id)      REFERENCES empresa (id),
    CONSTRAINT fk_bodega_sede         FOREIGN KEY (sede_id)         REFERENCES sede (id),
    CONSTRAINT fk_bodega_centro_costo FOREIGN KEY (centro_costo_id) REFERENCES centro_costo (id),
    CONSTRAINT uq_bodega_codigo       UNIQUE (empresa_id, codigo)
);
CREATE INDEX ix_bodega_empresa ON bodega (empresa_id);

-- bodega_responsable (← inv_bodegas_responsables)
CREATE TABLE bodega_responsable (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    bodega_id     BIGINT NOT NULL,
    tercero_id    BIGINT NOT NULL,
    predeterminado BOOLEAN NOT NULL DEFAULT FALSE,
    activo        BOOLEAN NOT NULL DEFAULT TRUE,
    created_at    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, deleted_at TIMESTAMP,
    CONSTRAINT fk_bodega_resp_bodega  FOREIGN KEY (bodega_id)  REFERENCES bodega (id),
    CONSTRAINT fk_bodega_resp_tercero FOREIGN KEY (tercero_id) REFERENCES tercero (id)
);

-- ubicacion (← inv_ubicaciones) — jerárquica dentro de la bodega
CREATE TABLE ubicacion (
    id                  BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    empresa_id          BIGINT       NOT NULL,
    bodega_id           BIGINT       NOT NULL,
    ubicacion_padre_id  BIGINT,
    codigo              VARCHAR(20)  NOT NULL,
    nombre              VARCHAR(150) NOT NULL,
    pasillo             INT, altura INT, posicion INT,
    izquierda INT, derecha INT, nivel INT,
    activo              BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at          TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP, deleted_at TIMESTAMP,
    CONSTRAINT fk_ubicacion_empresa FOREIGN KEY (empresa_id)         REFERENCES empresa (id),
    CONSTRAINT fk_ubicacion_bodega  FOREIGN KEY (bodega_id)          REFERENCES bodega (id),
    CONSTRAINT fk_ubicacion_padre   FOREIGN KEY (ubicacion_padre_id) REFERENCES ubicacion (id),
    CONSTRAINT uq_ubicacion_codigo  UNIQUE (bodega_id, codigo)
);
CREATE INDEX ix_ubicacion_bodega ON ubicacion (bodega_id);

-- lote (← inv_lotes)
CREATE TABLE lote (
    id                   BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    empresa_id           BIGINT       NOT NULL,
    producto_variante_id BIGINT,                       -- ← com_producto_variante_id
    codigo               VARCHAR(20)  NOT NULL,
    nombre               VARCHAR(150),
    fecha_fabricado      DATE,
    fecha_vencimiento    DATE,
    activo               BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at           TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at           TIMESTAMP, deleted_at TIMESTAMP,
    CONSTRAINT fk_lote_empresa  FOREIGN KEY (empresa_id)           REFERENCES empresa (id),
    CONSTRAINT fk_lote_variante FOREIGN KEY (producto_variante_id) REFERENCES producto_variante (id),
    CONSTRAINT uq_lote_codigo   UNIQUE (empresa_id, codigo)
);

-- movimiento_inventario (← inv_detalles_inventarios; APPEND-ONLY)
CREATE TABLE movimiento_inventario (
    id                   BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    empresa_id           BIGINT        NOT NULL,
    bodega_id            BIGINT        NOT NULL,
    ubicacion_id         BIGINT,
    producto_id          BIGINT        NOT NULL,
    producto_variante_id BIGINT,
    lote_id              BIGINT,
    tipo                 VARCHAR(15)   NOT NULL,        -- entrada | salida | ajuste | traslado
    fecha                DATE          NOT NULL,
    cantidad             NUMERIC(19,4) NOT NULL,
    costo_unitario       NUMERIC(19,4) NOT NULL DEFAULT 0,
    descuento_porcentaje NUMERIC(7,4),
    descuento_valor      NUMERIC(19,4),
    impuesto_id          BIGINT,                        -- ← com_impuesto_deduccion_id
    impuesto_porcentaje  NUMERIC(7,4),
    impuesto_valor       NUMERIC(19,4),
    subtotal             NUMERIC(19,4),
    total                NUMERIC(19,4),
    centro_costo_id      BIGINT,
    tercero_id           BIGINT,
    descripcion          VARCHAR(500),
    -- Trazabilidad del origen (compra/factura/ajuste), sin FK cruzada
    origen_modulo        VARCHAR(30),
    origen_id            BIGINT,
    created_at           TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    usuario_creacion     BIGINT,
    CONSTRAINT fk_movinv_empresa      FOREIGN KEY (empresa_id)           REFERENCES empresa (id),
    CONSTRAINT fk_movinv_bodega       FOREIGN KEY (bodega_id)            REFERENCES bodega (id),
    CONSTRAINT fk_movinv_ubicacion    FOREIGN KEY (ubicacion_id)         REFERENCES ubicacion (id),
    CONSTRAINT fk_movinv_producto     FOREIGN KEY (producto_id)          REFERENCES producto (id),
    CONSTRAINT fk_movinv_variante     FOREIGN KEY (producto_variante_id) REFERENCES producto_variante (id),
    CONSTRAINT fk_movinv_lote         FOREIGN KEY (lote_id)              REFERENCES lote (id),
    CONSTRAINT fk_movinv_impuesto     FOREIGN KEY (impuesto_id)          REFERENCES impuesto (id),
    CONSTRAINT fk_movinv_centro_costo FOREIGN KEY (centro_costo_id)      REFERENCES centro_costo (id),
    CONSTRAINT fk_movinv_tercero      FOREIGN KEY (tercero_id)           REFERENCES tercero (id),
    CONSTRAINT ck_movinv_tipo CHECK (tipo IN ('entrada', 'salida', 'ajuste', 'traslado'))
);
CREATE INDEX ix_movinv_empresa  ON movimiento_inventario (empresa_id);
CREATE INDEX ix_movinv_bodega   ON movimiento_inventario (bodega_id);
CREATE INDEX ix_movinv_producto ON movimiento_inventario (producto_id);
CREATE INDEX ix_movinv_origen   ON movimiento_inventario (origen_modulo, origen_id);
COMMENT ON TABLE movimiento_inventario IS 'Movimiento inmutable (append-only). Las correcciones son movimientos de reversa, no edición.';

-- saldo_inventario (← inv_productos_saldos) — PROYECCIÓN recalculable
CREATE TABLE saldo_inventario (
    id                   BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    empresa_id           BIGINT        NOT NULL,
    bodega_id            BIGINT        NOT NULL,
    ubicacion_id         BIGINT,
    lote_id              BIGINT,
    producto_id          BIGINT        NOT NULL,
    producto_variante_id BIGINT,
    cantidad             NUMERIC(19,4) NOT NULL DEFAULT 0,
    costo_promedio       NUMERIC(19,4) NOT NULL DEFAULT 0,
    valor_total          NUMERIC(19,4) NOT NULL DEFAULT 0,
    fecha_recalculo      TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_saldoinv_empresa   FOREIGN KEY (empresa_id)           REFERENCES empresa (id),
    CONSTRAINT fk_saldoinv_bodega    FOREIGN KEY (bodega_id)            REFERENCES bodega (id),
    CONSTRAINT fk_saldoinv_ubicacion FOREIGN KEY (ubicacion_id)         REFERENCES ubicacion (id),
    CONSTRAINT fk_saldoinv_lote      FOREIGN KEY (lote_id)              REFERENCES lote (id),
    CONSTRAINT fk_saldoinv_producto  FOREIGN KEY (producto_id)          REFERENCES producto (id),
    CONSTRAINT fk_saldoinv_variante  FOREIGN KEY (producto_variante_id) REFERENCES producto_variante (id),
    CONSTRAINT uq_saldo_inventario   UNIQUE (bodega_id, ubicacion_id, lote_id, producto_variante_id)
);
CREATE INDEX ix_saldoinv_empresa ON saldo_inventario (empresa_id);
COMMENT ON TABLE saldo_inventario IS 'Existencias por (producto/variante, bodega, ubicación, lote). Proyección recalculable desde movimiento_inventario.';
