package com.cloud_tecnoligical.nyxora_erp.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Getter;
import lombok.Setter;

/** Partida contable inmutable (R2DBC, ← con_detalles_contables). APPEND-ONLY: sin updated_at/deleted_at. */
@Table("movimiento_contable")
@Getter
@Setter
public class MovimientoContableEntity {

    @Id
    private Long id;

    @Column("empresa_id")          private Long empresa_id;
    @Column("comprobante_id")      private Long comprobante_id;
    @Column("cuenta_id")           private Long cuenta_id;
    @Column("tercero_id")          private Long tercero_id;
    @Column("centro_costo_id")     private Long centro_costo_id;
    @Column("proyecto_id")         private Long proyecto_id;
    @Column("recurso_id")          private Long recurso_id;
    @Column("descripcion")         private String descripcion;
    @Column("debito")              private BigDecimal debito;
    @Column("credito")             private BigDecimal credito;
    @Column("valor_base")          private BigDecimal valor_base;
    @Column("impuesto_id")         private Long impuesto_id;
    @Column("porcentaje_impuesto") private BigDecimal porcentaje_impuesto;
    @Column("valor_trm")           private BigDecimal valor_trm;
    @Column("valor_dolar")         private BigDecimal valor_dolar;
    @Column("created_at")          private LocalDateTime created_at;
    @Column("usuario_creacion")    private Long usuario_creacion;
}
