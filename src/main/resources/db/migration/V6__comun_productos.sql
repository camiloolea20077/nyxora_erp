-- ============================================================================
-- V6 — Catálogo de PRODUCTOS (módulo Común) — mapeado de com_productos real
-- Nyxora · PostgreSQL · esquema public
--
-- Conserva el subconjunto comercial/logístico. Se POSPONE lo clínico (cups/soat/iss,
-- forma_farmaceutica, posologia, via_administracion, medicamento_control, finalidades...)
-- al vertical salud, y la integración contable por categoría (con_cuenta_*, pre_*) a las
-- fases de Contabilidad/Presupuesto. Depende de: V1, V2 (unidad_medida, impuesto), V5.
-- ============================================================================

-- ----------------------------------------------------------------------------
-- categoria (← com_categorias) — jerárquica (nested set), subconjunto comercial
-- ----------------------------------------------------------------------------
CREATE TABLE categoria (
    id                   BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    empresa_id           BIGINT       NOT NULL,
    categoria_padre_id   BIGINT,                       -- ← com_categoria_id
    codigo               VARCHAR(20)  NOT NULL,
    nombre               VARCHAR(150) NOT NULL,
    tipo_producto        VARCHAR(20),                  -- ← tipo_producto_id (bien/servicio/activo...)
    metodo_costeo        VARCHAR(20),                  -- ← metodo_costeo_id (promedio/peps...)
    -- nested set (jerarquía rápida)
    izquierda            INT,
    derecha              INT,
    nivel                INT,
    activo               BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at           TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at           TIMESTAMP,
    deleted_at           TIMESTAMP,
    usuario_creacion     BIGINT,
    usuario_modificacion BIGINT,
    CONSTRAINT fk_categoria_empresa FOREIGN KEY (empresa_id)         REFERENCES empresa (id),
    CONSTRAINT fk_categoria_padre   FOREIGN KEY (categoria_padre_id) REFERENCES categoria (id),
    CONSTRAINT uq_categoria_codigo  UNIQUE (empresa_id, codigo)
);
CREATE INDEX ix_categoria_empresa ON categoria (empresa_id);
COMMENT ON TABLE categoria IS 'Categoría de producto (jerárquica). La config contable por categoría (con_cuenta_*) se agrega en la fase de Contabilidad.';

-- ----------------------------------------------------------------------------
-- producto (← com_productos) — subconjunto comercial/logístico
-- ----------------------------------------------------------------------------
CREATE TABLE producto (
    id                     BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    empresa_id             BIGINT       NOT NULL,
    categoria_id           BIGINT,                       -- ← com_categoria_id
    codigo                 VARCHAR(40)  NOT NULL,
    codigo_unspsc          VARCHAR(25),                  -- ← codigo_unspsc
    nombre                 VARCHAR(150) NOT NULL,
    descripcion            VARCHAR(500),
    tipo                   VARCHAR(10)  NOT NULL DEFAULT 'bien', -- bien | servicio
    es_compuesto           BOOLEAN      NOT NULL DEFAULT FALSE,  -- ← compuesto
    -- Unidades y contenido
    unidad_mayor_id        BIGINT,                       -- ← unidad_mayor_id (FK unidad_medida)
    unidad_menor_id        BIGINT,                       -- ← unidad_menor_id (FK unidad_medida)
    contenido              NUMERIC(19,4),                -- ← contenido
    -- Inventario / logística
    maneja_inventario      BOOLEAN      NOT NULL DEFAULT TRUE,
    maneja_lote            BOOLEAN      NOT NULL DEFAULT FALSE, -- ← maneja_lote
    maneja_desperdicio     BOOLEAN      NOT NULL DEFAULT FALSE, -- ← maneja_desperdicio
    es_devolutivo          BOOLEAN      NOT NULL DEFAULT FALSE, -- ← es_devolutivo
    stock_minimo           NUMERIC(19,4),                -- ← stock_minimo
    stock_maximo           NUMERIC(19,4),                -- ← stock_maximo
    tiempo_reabastecimiento INT,                         -- ← tiempo_reabastecimiento (días)
    -- Comercial / tributario
    impuesto_id            BIGINT,                       -- ← com_impuesto_deduccion_id (IVA por defecto)
    discrimina_iva         BOOLEAN      NOT NULL DEFAULT FALSE, -- ← discrimina_iva
    aplica_impuesto_bolsa  BOOLEAN      NOT NULL DEFAULT FALSE, -- ← aplica_impuesto_bolsa
    tarifa_maxima          NUMERIC(19,4),                -- ← tarifa_maxima
    es_pos                 BOOLEAN      NOT NULL DEFAULT FALSE, -- ← es_pos
    recurso_id             BIGINT,                       -- ← cos_recurso_id (FK futura a Costos)
    imagen                 JSONB,                        -- ← imagen (json)
    activo                 BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at             TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at             TIMESTAMP,
    deleted_at             TIMESTAMP,
    usuario_creacion       BIGINT,
    usuario_modificacion   BIGINT,
    CONSTRAINT fk_producto_empresa      FOREIGN KEY (empresa_id)      REFERENCES empresa (id),
    CONSTRAINT fk_producto_categoria    FOREIGN KEY (categoria_id)    REFERENCES categoria (id),
    CONSTRAINT fk_producto_unidad_mayor FOREIGN KEY (unidad_mayor_id) REFERENCES unidad_medida (id),
    CONSTRAINT fk_producto_unidad_menor FOREIGN KEY (unidad_menor_id) REFERENCES unidad_medida (id),
    CONSTRAINT fk_producto_impuesto     FOREIGN KEY (impuesto_id)     REFERENCES impuesto (id),
    CONSTRAINT uq_producto_codigo UNIQUE (empresa_id, codigo),
    CONSTRAINT ck_producto_tipo   CHECK (tipo IN ('bien', 'servicio'))
);
CREATE INDEX ix_producto_empresa   ON producto (empresa_id);
CREATE INDEX ix_producto_categoria ON producto (categoria_id);
COMMENT ON TABLE producto IS 'Catálogo de bienes/servicios (subconjunto comercial del com_productos real). Lo clínico va al vertical salud.';

-- ----------------------------------------------------------------------------
-- producto_variante (← com_productos_variantes)
-- ----------------------------------------------------------------------------
CREATE TABLE producto_variante (
    id               BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    producto_id      BIGINT       NOT NULL,
    sku_plu          VARCHAR(15),                  -- ← sku_plu
    codigo_barra     VARCHAR(13),                  -- ← codigo_barra (EAN-13)
    precio_adicional NUMERIC(19,4),                -- ← precio_adicional
    costo            NUMERIC(19,4),                -- ← costo
    imagen           JSONB,                        -- ← imagen
    activo           BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at       TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at       TIMESTAMP,
    deleted_at       TIMESTAMP,
    CONSTRAINT fk_producto_variante_producto FOREIGN KEY (producto_id) REFERENCES producto (id)
);
CREATE INDEX ix_producto_variante_producto ON producto_variante (producto_id);

-- ----------------------------------------------------------------------------
-- producto_proveedor (← com_productos_proveedores)
-- ----------------------------------------------------------------------------
CREATE TABLE producto_proveedor (
    id                BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    producto_id       BIGINT       NOT NULL,
    proveedor_id      BIGINT       NOT NULL,        -- ← com_tercero_id (rol proveedor)
    codigo_producto   VARCHAR(20),                  -- ← codigo_producto (código del proveedor)
    cantidad_minima   NUMERIC(19,4),                -- ← cantidad_minima
    plazo_entrega     INT,                          -- ← plazo_entrega (días)
    activo            BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at        TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at        TIMESTAMP,
    deleted_at        TIMESTAMP,
    CONSTRAINT fk_producto_proveedor_producto  FOREIGN KEY (producto_id)  REFERENCES producto (id),
    CONSTRAINT fk_producto_proveedor_tercero   FOREIGN KEY (proveedor_id) REFERENCES tercero (id),
    CONSTRAINT uq_producto_proveedor UNIQUE (producto_id, proveedor_id)
);
CREATE INDEX ix_producto_proveedor_producto ON producto_proveedor (producto_id);

-- ----------------------------------------------------------------------------
-- producto_impuesto (impuestos adicionales del producto, además del por defecto)
-- ----------------------------------------------------------------------------
CREATE TABLE producto_impuesto (
    producto_id BIGINT NOT NULL,
    impuesto_id BIGINT NOT NULL,
    CONSTRAINT pk_producto_impuesto PRIMARY KEY (producto_id, impuesto_id),
    CONSTRAINT fk_producto_impuesto_producto FOREIGN KEY (producto_id) REFERENCES producto (id),
    CONSTRAINT fk_producto_impuesto_impuesto FOREIGN KEY (impuesto_id) REFERENCES impuesto (id)
);
