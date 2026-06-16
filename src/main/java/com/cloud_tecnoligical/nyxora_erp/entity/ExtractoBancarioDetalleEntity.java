package com.cloud_tecnoligical.nyxora_erp.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Getter;
import lombok.Setter;

/** Movimiento de un extracto bancario (R2DBC). */
@Table("extracto_bancario_detalle")
@Getter
@Setter
public class ExtractoBancarioDetalleEntity {

    @Id
    private Long id;

    @Column("extracto_bancario_id") private Long extracto_bancario_id;
    @Column("fecha")                private LocalDate fecha;
    @Column("descripcion")          private String descripcion;
    @Column("valor")                private BigDecimal valor;
    @Column("tipo")                 private String tipo;
    @Column("conciliado")           private Boolean conciliado;
    @Column("created_at")           private LocalDateTime created_at;
    @Column("deleted_at")           private LocalDateTime deleted_at;
}
