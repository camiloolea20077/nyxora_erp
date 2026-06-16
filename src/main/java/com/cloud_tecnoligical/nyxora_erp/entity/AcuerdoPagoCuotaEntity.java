package com.cloud_tecnoligical.nyxora_erp.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Getter;
import lombok.Setter;

/** Cuota de un acuerdo de pago (R2DBC). Estados: pendiente → pagada / vencida. */
@Table("acuerdo_pago_cuota")
@Getter
@Setter
public class AcuerdoPagoCuotaEntity {

    @Id
    private Long id;

    @Column("acuerdo_pago_id")  private Long acuerdo_pago_id;
    @Column("numero_cuota")     private Integer numero_cuota;
    @Column("valor")            private BigDecimal valor;
    @Column("fecha_aplicacion") private LocalDate fecha_aplicacion;
    @Column("estado")           private String estado;
    @Column("created_at")       private LocalDateTime created_at;
    @Column("updated_at")       private LocalDateTime updated_at;
    @Column("deleted_at")       private LocalDateTime deleted_at;
}
