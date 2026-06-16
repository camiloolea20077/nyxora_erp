package com.cloud_tecnoligical.nyxora_erp.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Getter;
import lombok.Setter;

/** Detalle de depreciación (append-only). R2DBC. */
@Table("depreciacion")
@Getter
@Setter
public class DepreciacionEntity {

    @Id
    private Long id;

    @Column("empresa_id")           private Long empresa_id;
    @Column("activo_fijo_id")       private Long activo_fijo_id;
    @Column("fecha_aplicacion")     private LocalDate fecha_aplicacion;
    @Column("valor_depreciacion")   private BigDecimal valor_depreciacion;
    @Column("cuota_depreciacion")   private BigDecimal cuota_depreciacion;
    @Column("periodo_amortizacion") private Integer periodo_amortizacion;
    @Column("unidades_producidas")  private Integer unidades_producidas;
    @Column("created_at")           private LocalDateTime created_at;
    @Column("usuario_creacion")     private Long usuario_creacion;
}
