package com.cloud_tecnoligical.nyxora_erp.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Getter;
import lombok.Setter;

/** Extracto bancario cargado para conciliar (R2DBC). */
@Table("extracto_bancario")
@Getter
@Setter
public class ExtractoBancarioEntity {

    @Id
    private Long id;

    @Column("empresa_id")           private Long empresa_id;
    @Column("cuenta_bancaria_id")   private Long cuenta_bancaria_id;
    @Column("fecha_inicial")        private LocalDate fecha_inicial;
    @Column("fecha_final")          private LocalDate fecha_final;
    @Column("saldo_inicial")        private BigDecimal saldo_inicial;
    @Column("saldo_final")          private BigDecimal saldo_final;
    @Column("valor_conciliado")     private BigDecimal valor_conciliado;
    @Column("valor_no_conciliado")  private BigDecimal valor_no_conciliado;
    @Column("created_at")           private LocalDateTime created_at;
    @Column("updated_at")           private LocalDateTime updated_at;
    @Column("deleted_at")           private LocalDateTime deleted_at;
}
