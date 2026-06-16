package com.cloud_tecnoligical.nyxora_erp.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Getter;
import lombok.Setter;

/** Retención aplicada a una obligación de pago (R2DBC). */
@Table("obligacion_pago_retencion")
@Getter
@Setter
public class ObligacionPagoRetencionEntity {

    @Id
    private Long id;

    @Column("obligacion_pago_id")   private Long obligacion_pago_id;
    @Column("impuesto_id")          private Long impuesto_id;
    @Column("base")                 private BigDecimal base;
    @Column("limite")               private String limite;
    @Column("valor")                private BigDecimal valor;
    @Column("created_at")           private LocalDateTime created_at;
    @Column("deleted_at")           private LocalDateTime deleted_at;
}
