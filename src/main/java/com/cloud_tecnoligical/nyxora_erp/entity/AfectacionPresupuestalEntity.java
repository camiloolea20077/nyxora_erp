package com.cloud_tecnoligical.nyxora_erp.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Getter;
import lombok.Setter;

/**
 * Afectación presupuestal (R2DBC, APPEND-ONLY). Cadena CDP→compromiso→obligación→pago (gasto)
 * y reconocimiento→recaudo (ingreso). tipo_operacion CHECK en BD.
 */
@Table("afectacion_presupuestal")
@Getter
@Setter
public class AfectacionPresupuestalEntity {

    @Id
    private Long id;

    @Column("empresa_id")               private Long empresa_id;
    @Column("rubro_presupuestal_id")    private Long rubro_presupuestal_id;
    @Column("tipo_operacion")           private String tipo_operacion;
    @Column("tercero_id")               private Long tercero_id;
    @Column("centro_costo_id")          private Long centro_costo_id;
    @Column("proyecto_id")              private Long proyecto_id;
    @Column("fuente_financiamiento_id") private Long fuente_financiamiento_id;
    @Column("cpc_id")                   private Long cpc_id;
    @Column("descripcion")              private String descripcion;
    @Column("valor")                    private BigDecimal valor;
    @Column("subtotal")                 private BigDecimal subtotal;
    @Column("saldo")                    private BigDecimal saldo;
    @Column("origen_modulo")            private String origen_modulo;
    @Column("origen_id")                private Long origen_id;
    @Column("created_at")               private LocalDateTime created_at;
    @Column("usuario_creacion")         private Long usuario_creacion;
}
