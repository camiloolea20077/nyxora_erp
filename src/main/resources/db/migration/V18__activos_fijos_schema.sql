-- ============================================================================
-- V18 — Activos Fijos — mapeado de acf_activos_fijos / acf_detalles_depreciaciones /
--      acf_responsables_activos_fijos / acf_polizas_seguros
-- Nyxora · PostgreSQL · esquema public
-- Depende de: V1, V2 (unidad_medida), V5 (tercero), V6 (producto), V7 (centro_costo), V9 (marca, bodega).
-- ============================================================================

-- poliza_seguro (maestro de pólizas; el real las tenía dispersas en com_*/acf_*)
CREATE TABLE poliza_seguro (
    id               BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    empresa_id       BIGINT       NOT NULL,
    numero           VARCHAR(60)  NOT NULL,
    aseguradora_id   BIGINT,                          -- tercero
    tipo             VARCHAR(40),
    fecha_inicio     DATE,
    fecha_fin        DATE,
    valor_asegurado  NUMERIC(19,4),
    activo           BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, updated_at TIMESTAMP, deleted_at TIMESTAMP,
    CONSTRAINT fk_poliza_empresa     FOREIGN KEY (empresa_id)     REFERENCES empresa (id),
    CONSTRAINT fk_poliza_aseguradora FOREIGN KEY (aseguradora_id) REFERENCES tercero (id),
    CONSTRAINT uq_poliza_numero      UNIQUE (empresa_id, numero)
);

-- activo_fijo (← acf_activos_fijos)
CREATE TABLE activo_fijo (
    id                   BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    empresa_id           BIGINT        NOT NULL,
    producto_id          BIGINT,                       -- ← com_producto_id
    codigo               VARCHAR(40),
    codigo_unspsc        VARCHAR(25),
    codigo_barra         VARCHAR(100),
    nombre               VARCHAR(150) NOT NULL,
    descripcion          VARCHAR(500),
    marca_id             BIGINT,                       -- ← inv_marca_id
    unidad_mayor_id      BIGINT,
    numero_serie         VARCHAR(100),
    modelo               VARCHAR(250),
    bodega_id            BIGINT,                       -- ← inv_bodega_id
    centro_costo_id      BIGINT,                       -- ← centro_costo_id
    proveedor_id         BIGINT,                       -- ← com_tercero_id
    numero_factura       VARCHAR(40),
    fecha_factura        DATE,
    valor_compra         NUMERIC(19,4),
    valor_salvamento     NUMERIC(19,4),
    porcentaje_salvamento NUMERIC(7,4),
    metodo_depreciacion  VARCHAR(30),                  -- ← metodo_depreciacion
    tipo_depreciacion    VARCHAR(30),                  -- ← tipo_depreciacion
    valor_depreciacion   NUMERIC(19,4),
    deterioro            NUMERIC(19,4),
    valor_actual         NUMERIC(19,4),
    avaluo               NUMERIC(19,4),
    vida_util            INT,
    meses_depreciados    INT,
    capitalizado         NUMERIC(19,4),
    estado_activo        VARCHAR(30),                  -- ← estado_activo_fijo
    fecha_salida_servicio DATE,
    activo               BOOLEAN       NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, updated_at TIMESTAMP, deleted_at TIMESTAMP,
    usuario_creacion BIGINT, usuario_modificacion BIGINT,
    CONSTRAINT fk_acf_empresa      FOREIGN KEY (empresa_id)      REFERENCES empresa (id),
    CONSTRAINT fk_acf_producto     FOREIGN KEY (producto_id)     REFERENCES producto (id),
    CONSTRAINT fk_acf_marca        FOREIGN KEY (marca_id)        REFERENCES marca (id),
    CONSTRAINT fk_acf_unidad       FOREIGN KEY (unidad_mayor_id) REFERENCES unidad_medida (id),
    CONSTRAINT fk_acf_bodega       FOREIGN KEY (bodega_id)       REFERENCES bodega (id),
    CONSTRAINT fk_acf_centro_costo FOREIGN KEY (centro_costo_id) REFERENCES centro_costo (id),
    CONSTRAINT fk_acf_proveedor    FOREIGN KEY (proveedor_id)    REFERENCES tercero (id),
    CONSTRAINT uq_acf_codigo       UNIQUE (empresa_id, codigo)
);
CREATE INDEX ix_acf_empresa ON activo_fijo (empresa_id);

-- depreciacion (← acf_detalles_depreciaciones; APPEND-ONLY)
CREATE TABLE depreciacion (
    id                   BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    empresa_id           BIGINT        NOT NULL,
    activo_fijo_id       BIGINT        NOT NULL,
    fecha_aplicacion     DATE          NOT NULL,
    valor_depreciacion   NUMERIC(19,4) NOT NULL DEFAULT 0,
    cuota_depreciacion   NUMERIC(19,4),
    periodo_amortizacion INT,
    unidades_producidas  INT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    usuario_creacion BIGINT,
    CONSTRAINT fk_depr_empresa FOREIGN KEY (empresa_id)     REFERENCES empresa (id),
    CONSTRAINT fk_depr_activo  FOREIGN KEY (activo_fijo_id) REFERENCES activo_fijo (id)
);
CREATE INDEX ix_depr_activo ON depreciacion (activo_fijo_id);

-- activo_fijo_responsable (← acf_responsables_activos_fijos)
CREATE TABLE activo_fijo_responsable (
    id             BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    activo_fijo_id BIGINT NOT NULL,
    tercero_id     BIGINT NOT NULL,
    activo         BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, deleted_at TIMESTAMP,
    CONSTRAINT fk_acf_resp_activo  FOREIGN KEY (activo_fijo_id) REFERENCES activo_fijo (id),
    CONSTRAINT fk_acf_resp_tercero FOREIGN KEY (tercero_id)     REFERENCES tercero (id)
);

-- activo_fijo_poliza (← acf_polizas_seguros)
CREATE TABLE activo_fijo_poliza (
    id               BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    activo_fijo_id   BIGINT NOT NULL,
    poliza_seguro_id BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, deleted_at TIMESTAMP,
    CONSTRAINT fk_acf_pol_activo FOREIGN KEY (activo_fijo_id)   REFERENCES activo_fijo (id),
    CONSTRAINT fk_acf_pol_poliza FOREIGN KEY (poliza_seguro_id) REFERENCES poliza_seguro (id),
    CONSTRAINT uq_acf_poliza     UNIQUE (activo_fijo_id, poliza_seguro_id)
);
