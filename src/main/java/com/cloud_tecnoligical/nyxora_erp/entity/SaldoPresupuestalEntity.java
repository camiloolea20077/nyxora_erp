package com.cloud_tecnoligical.nyxora_erp.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Getter;
import lombok.Setter;

/** Saldo presupuestal por (rubro, anio, mes) (R2DBC). Apropiación = entrada; ejecución = proyección. */
@Table("saldo_presupuestal")
@Getter
@Setter
public class SaldoPresupuestalEntity {

    @Id
    private Long id;

    @Column("empresa_id")            private Long empresa_id;
    @Column("rubro_presupuestal_id") private Long rubro_presupuestal_id;
    @Column("anio")                  private Integer anio;
    @Column("mes")                   private Integer mes;
    @Column("plan_inicial")          private BigDecimal plan_inicial;
    @Column("adiciones")             private BigDecimal adiciones;
    @Column("reducciones")           private BigDecimal reducciones;
    @Column("aplazamientos")         private BigDecimal aplazamientos;
    @Column("creditos")              private BigDecimal creditos;
    @Column("contra_creditos")       private BigDecimal contra_creditos;
    @Column("disponibilidad")        private BigDecimal disponibilidad;
    @Column("compromiso")            private BigDecimal compromiso;
    @Column("obligacion")            private BigDecimal obligacion;
    @Column("pagado")                private BigDecimal pagado;
    @Column("reconocimientos")       private BigDecimal reconocimientos;
    @Column("recaudos")              private BigDecimal recaudos;
    @Column("fecha_recalculo")       private LocalDateTime fecha_recalculo;
}
