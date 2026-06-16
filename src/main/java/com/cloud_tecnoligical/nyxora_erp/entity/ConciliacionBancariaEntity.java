package com.cloud_tecnoligical.nyxora_erp.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Getter;
import lombok.Setter;

/** Conciliación de un movimiento del extracto contra los libros (R2DBC). Append-only. */
@Table("conciliacion_bancaria")
@Getter
@Setter
public class ConciliacionBancariaEntity {

    @Id
    private Long id;

    @Column("empresa_id")           private Long empresa_id;
    @Column("cuenta_bancaria_id")   private Long cuenta_bancaria_id;
    @Column("extracto_detalle_id")  private Long extracto_detalle_id;
    @Column("cuenta_contable_id")   private Long cuenta_contable_id;
    @Column("valor_conciliado")     private BigDecimal valor_conciliado;
    @Column("fecha")                private LocalDate fecha;
    @Column("origen_modulo")        private String origen_modulo;
    @Column("origen_id")            private Long origen_id;
    @Column("created_at")           private LocalDateTime created_at;
    @Column("deleted_at")           private LocalDateTime deleted_at;
}
