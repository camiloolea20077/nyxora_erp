package com.cloud_tecnoligical.nyxora_erp.entity;

import java.math.BigDecimal;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Getter;
import lombok.Setter;

/** PAC — Plan Anualizado de Caja por (rubro, anio, mes 1-12) (R2DBC). */
@Table("pac_presupuestal")
@Getter
@Setter
public class PacPresupuestalEntity {

    @Id
    private Long id;

    @Column("empresa_id")            private Long empresa_id;
    @Column("rubro_presupuestal_id") private Long rubro_presupuestal_id;
    @Column("anio")                  private Integer anio;
    @Column("mes")                   private Integer mes;
    @Column("valor")                 private BigDecimal valor;
}
