-- ============================================================================
-- V21 — Nómina (núcleo funcional) — mapeado de nom_* (60 tablas → núcleo limpio)
-- Nyxora · PostgreSQL · esquema public
--
-- Núcleo: cargo, grupo_nomina, vinculacion, concepto_nomina, novedad_nomina,
-- liquidacion_nomina + detalle (append-only), aporte_pila. Las variantes muy específicas
-- (catálogos finos, PILA detallada por tipos) se modelan en una 2ª pasada.
-- Empleado = tercero (rol empleado). Depende de: V1, V5, V7, V8, V15, V20.
-- ============================================================================

-- cargo (← nom_cargos)
CREATE TABLE cargo (
    id               BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    empresa_id       BIGINT       NOT NULL,
    codigo           VARCHAR(20)  NOT NULL,
    nombre           VARCHAR(150) NOT NULL,            -- ← cargo
    nivel_cargo      VARCHAR(40),
    grado            VARCHAR(40),
    tipo_remuneracion VARCHAR(40),
    sueldo_basico    NUMERIC(19,4),
    sueldo_maximo    NUMERIC(19,4),
    mision           TEXT, descripcion TEXT,
    activo           BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, updated_at TIMESTAMP, deleted_at TIMESTAMP,
    usuario_creacion BIGINT, usuario_modificacion BIGINT,
    CONSTRAINT fk_cargo_empresa FOREIGN KEY (empresa_id) REFERENCES empresa (id),
    CONSTRAINT uq_cargo_codigo  UNIQUE (empresa_id, codigo)
);

-- grupo_nomina (← nom_grupos_nomina)
CREATE TABLE grupo_nomina (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    empresa_id BIGINT NOT NULL,
    codigo VARCHAR(20) NOT NULL, nombre VARCHAR(150) NOT NULL,
    frecuencia_pago VARCHAR(20),
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, updated_at TIMESTAMP, deleted_at TIMESTAMP,
    CONSTRAINT fk_grupo_nomina_empresa FOREIGN KEY (empresa_id) REFERENCES empresa (id),
    CONSTRAINT uq_grupo_nomina_codigo UNIQUE (empresa_id, codigo)
);

-- vinculacion (← nom_vinculaciones)
CREATE TABLE vinculacion (
    id                   BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    empresa_id           BIGINT       NOT NULL,
    empleado_id          BIGINT       NOT NULL,        -- ← com_tercero_empleado_id
    cargo_id             BIGINT,
    grupo_nomina_id      BIGINT,
    codigo               VARCHAR(20),
    fecha                DATE,
    fecha_fin            DATE,
    tipo_vinculacion     VARCHAR(40),                  -- ← nom_tipo_vinculacion_id
    tipo_contrato        VARCHAR(40),
    sueldo               NUMERIC(19,4),
    hora_trabajo         INT,
    periodo_prueba       BOOLEAN NOT NULL DEFAULT FALSE,
    fecha_fin_periodo_prueba DATE,
    frecuencia_pago      VARCHAR(20),
    jefe_id              BIGINT,                        -- ← com_tercero_jefe_id
    sede_id              BIGINT,
    dependencia_id       BIGINT,
    municipio_vinculacion_id BIGINT,
    tipo_cotizante       VARCHAR(20),
    estado_vinculacion   VARCHAR(20),
    objeto               TEXT,
    temporal             BOOLEAN NOT NULL DEFAULT FALSE,
    activo               BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, updated_at TIMESTAMP, deleted_at TIMESTAMP,
    usuario_creacion BIGINT, usuario_modificacion BIGINT,
    CONSTRAINT fk_vinc_empresa     FOREIGN KEY (empresa_id)              REFERENCES empresa (id),
    CONSTRAINT fk_vinc_empleado    FOREIGN KEY (empleado_id)             REFERENCES tercero (id),
    CONSTRAINT fk_vinc_cargo       FOREIGN KEY (cargo_id)                REFERENCES cargo (id),
    CONSTRAINT fk_vinc_grupo       FOREIGN KEY (grupo_nomina_id)         REFERENCES grupo_nomina (id),
    CONSTRAINT fk_vinc_jefe        FOREIGN KEY (jefe_id)                 REFERENCES tercero (id),
    CONSTRAINT fk_vinc_sede        FOREIGN KEY (sede_id)                 REFERENCES sede (id),
    CONSTRAINT fk_vinc_dependencia FOREIGN KEY (dependencia_id)          REFERENCES dependencia (id),
    CONSTRAINT fk_vinc_municipio   FOREIGN KEY (municipio_vinculacion_id) REFERENCES municipio (id)
);
CREATE INDEX ix_vinc_empresa  ON vinculacion (empresa_id);
CREATE INDEX ix_vinc_empleado ON vinculacion (empleado_id);

-- concepto_nomina (← nom_conceptos)
CREATE TABLE concepto_nomina (
    id                   BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    empresa_id           BIGINT       NOT NULL,
    codigo               VARCHAR(20)  NOT NULL,
    nombre               VARCHAR(250) NOT NULL,
    frecuencia           VARCHAR(20),
    clase                VARCHAR(20),                  -- devengado | deduccion | provision | aporte
    formula              TEXT,
    cuenta_credito_id    BIGINT,                       -- ← con_plan_contable_credito_id
    cuenta_patrono_id    BIGINT,                       -- ← con_plan_contable_patrono_id
    rubro_presupuestal_id BIGINT,
    fuente_financiamiento_id BIGINT,
    tercero_id           BIGINT,
    activo               BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, updated_at TIMESTAMP, deleted_at TIMESTAMP,
    CONSTRAINT fk_concepto_empresa  FOREIGN KEY (empresa_id)              REFERENCES empresa (id),
    CONSTRAINT fk_concepto_cred     FOREIGN KEY (cuenta_credito_id)       REFERENCES cuenta (id),
    CONSTRAINT fk_concepto_patrono  FOREIGN KEY (cuenta_patrono_id)       REFERENCES cuenta (id),
    CONSTRAINT fk_concepto_rubro    FOREIGN KEY (rubro_presupuestal_id)   REFERENCES rubro_presupuestal (id),
    CONSTRAINT fk_concepto_fuente   FOREIGN KEY (fuente_financiamiento_id) REFERENCES fuente_financiamiento (id),
    CONSTRAINT fk_concepto_tercero  FOREIGN KEY (tercero_id)              REFERENCES tercero (id),
    CONSTRAINT uq_concepto_codigo   UNIQUE (empresa_id, codigo)
);

-- novedad_nomina (← nom_novedades; subconjunto funcional, incl. embargos)
CREATE TABLE novedad_nomina (
    id                   BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    empresa_id           BIGINT       NOT NULL,
    vinculacion_id       BIGINT       NOT NULL,
    concepto_nomina_id   BIGINT       NOT NULL,
    tercero_id           BIGINT,
    descripcion          TEXT,
    cantidad_valor       NUMERIC(19,4),
    fecha_inicial        DATE, fecha_final DATE, fecha_aplicada DATE,
    numero_cuota         NUMERIC(10,2),
    dias                 INT,
    tipo_ausentismo      VARCHAR(30),
    -- embargos
    tipo_embargo         VARCHAR(30),
    expediente           VARCHAR(100),
    demandante           VARCHAR(191),
    banco_id             BIGINT,
    numero_cuenta_bancaria VARCHAR(20),
    estado_novedad       VARCHAR(20),
    anulado              BOOLEAN NOT NULL DEFAULT FALSE,
    activo               BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, updated_at TIMESTAMP, deleted_at TIMESTAMP,
    usuario_creacion BIGINT, usuario_modificacion BIGINT,
    CONSTRAINT fk_nov_empresa     FOREIGN KEY (empresa_id)         REFERENCES empresa (id),
    CONSTRAINT fk_nov_vinculacion FOREIGN KEY (vinculacion_id)     REFERENCES vinculacion (id),
    CONSTRAINT fk_nov_concepto    FOREIGN KEY (concepto_nomina_id) REFERENCES concepto_nomina (id),
    CONSTRAINT fk_nov_tercero     FOREIGN KEY (tercero_id)         REFERENCES tercero (id),
    CONSTRAINT fk_nov_banco       FOREIGN KEY (banco_id)           REFERENCES banco (id)
);
CREATE INDEX ix_nov_empresa     ON novedad_nomina (empresa_id);
CREATE INDEX ix_nov_vinculacion ON novedad_nomina (vinculacion_id);

-- liquidacion_nomina (encabezado del proceso de liquidación de un periodo)
CREATE TABLE liquidacion_nomina (
    id               BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    empresa_id       BIGINT      NOT NULL,
    grupo_nomina_id  BIGINT,
    anio             INT         NOT NULL,
    mes              INT         NOT NULL,
    periodo          VARCHAR(20),
    fecha            DATE        NOT NULL,
    estado           VARCHAR(20) NOT NULL DEFAULT 'abierto',
    activo           BOOLEAN     NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, updated_at TIMESTAMP, deleted_at TIMESTAMP,
    usuario_creacion BIGINT, usuario_modificacion BIGINT,
    CONSTRAINT fk_liq_empresa FOREIGN KEY (empresa_id)      REFERENCES empresa (id),
    CONSTRAINT fk_liq_grupo   FOREIGN KEY (grupo_nomina_id) REFERENCES grupo_nomina (id),
    CONSTRAINT ck_liq_estado  CHECK (estado IN ('abierto','liquidado','revisado','contabilizado','cerrado','anulado')),
    CONSTRAINT ck_liq_mes     CHECK (mes BETWEEN 1 AND 12)
);
CREATE INDEX ix_liq_empresa ON liquidacion_nomina (empresa_id);

-- liquidacion_nomina_detalle (← nom_detalles_liquidaciones; APPEND-ONLY)
CREATE TABLE liquidacion_nomina_detalle (
    id                   BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    empresa_id           BIGINT       NOT NULL,
    liquidacion_nomina_id BIGINT      NOT NULL,
    empleado_id          BIGINT       NOT NULL,        -- ← com_tercero_empleado_id
    vinculacion_id       BIGINT,
    concepto_nomina_id   BIGINT       NOT NULL,
    centro_costo_id      BIGINT,
    fecha_liquidacion    TIMESTAMP,
    fecha_inicial        DATE, fecha_final DATE,
    base                 NUMERIC(19,4),
    porcentaje           NUMERIC(9,4),
    cantidad             NUMERIC(19,4),
    valor                NUMERIC(19,4) NOT NULL DEFAULT 0,
    valor_empleado       NUMERIC(19,4),
    valor_patrono        NUMERIC(19,4),
    valor_entidad        NUMERIC(19,4),
    tipo_aporte          VARCHAR(30),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    usuario_creacion BIGINT,
    CONSTRAINT fk_liqdet_empresa     FOREIGN KEY (empresa_id)            REFERENCES empresa (id),
    CONSTRAINT fk_liqdet_liq         FOREIGN KEY (liquidacion_nomina_id) REFERENCES liquidacion_nomina (id),
    CONSTRAINT fk_liqdet_empleado    FOREIGN KEY (empleado_id)           REFERENCES tercero (id),
    CONSTRAINT fk_liqdet_vinculacion FOREIGN KEY (vinculacion_id)        REFERENCES vinculacion (id),
    CONSTRAINT fk_liqdet_concepto    FOREIGN KEY (concepto_nomina_id)    REFERENCES concepto_nomina (id),
    CONSTRAINT fk_liqdet_centro      FOREIGN KEY (centro_costo_id)       REFERENCES centro_costo (id)
);
CREATE INDEX ix_liqdet_liq      ON liquidacion_nomina_detalle (liquidacion_nomina_id);
CREATE INDEX ix_liqdet_empleado ON liquidacion_nomina_detalle (empleado_id);

-- aporte_pila (seguridad social, ← nom_pila_*; resumen por empleado/liquidación)
CREATE TABLE aporte_pila (
    id                   BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    empresa_id           BIGINT       NOT NULL,
    liquidacion_nomina_id BIGINT      NOT NULL,
    empleado_id          BIGINT       NOT NULL,
    tipo_aporte          VARCHAR(30)  NOT NULL,        -- salud | pension | arl | ccf | sena | icbf
    ibc                  NUMERIC(19,4),
    valor_empleado       NUMERIC(19,4),
    valor_patrono        NUMERIC(19,4),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_pila_empresa  FOREIGN KEY (empresa_id)            REFERENCES empresa (id),
    CONSTRAINT fk_pila_liq      FOREIGN KEY (liquidacion_nomina_id) REFERENCES liquidacion_nomina (id),
    CONSTRAINT fk_pila_empleado FOREIGN KEY (empleado_id)           REFERENCES tercero (id)
);
CREATE INDEX ix_pila_liq ON aporte_pila (liquidacion_nomina_id);
