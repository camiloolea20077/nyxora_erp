package com.cloud_tecnoligical.nyxora_erp.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Getter;
import lombok.Setter;

/** Cuenta por cobrar (R2DBC). Estados: vigente → en_acuerdo → pagada / anulada. */
@Table("cuenta_por_cobrar")
@Getter
@Setter
public class CuentaPorCobrarEntity {

    @Id
    private Long id;

    @Column("empresa_id")               private Long empresa_id;
    @Column("cliente_id")               private Long cliente_id;
    @Column("factura_id")               private Long factura_id;
    @Column("cuenta_id")                private Long cuenta_id;
    @Column("fecha_emision")            private LocalDate fecha_emision;
    @Column("fecha_vencimiento")        private LocalDate fecha_vencimiento;
    @Column("dias")                     private Integer dias;
    @Column("valor_total")              private BigDecimal valor_total;
    @Column("valor_interes")            private BigDecimal valor_interes;
    @Column("saldo")                    private BigDecimal saldo;
    @Column("fecha_ultima_liquidacion") private LocalDate fecha_ultima_liquidacion;
    @Column("estado")                   private String estado;
    @Column("activo")                   private Boolean activo;
    @Column("created_at")               private LocalDateTime created_at;
    @Column("updated_at")               private LocalDateTime updated_at;
    @Column("deleted_at")               private LocalDateTime deleted_at;
    @Column("usuario_creacion")         private Long usuario_creacion;
    @Column("usuario_modificacion")     private Long usuario_modificacion;
}
