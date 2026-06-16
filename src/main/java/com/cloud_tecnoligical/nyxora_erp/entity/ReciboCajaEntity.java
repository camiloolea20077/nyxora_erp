package com.cloud_tecnoligical.nyxora_erp.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Getter;
import lombok.Setter;

/** Recibo de caja (R2DBC). Estados: registrado → anulado. */
@Table("recibo_caja")
@Getter
@Setter
public class ReciboCajaEntity {

    @Id
    private Long id;

    @Column("empresa_id")           private Long empresa_id;
    @Column("caja_id")              private Long caja_id;
    @Column("tipo_documento_id")    private Long tipo_documento_id;
    @Column("numero")               private String numero;
    @Column("cliente_id")           private Long cliente_id;
    @Column("fecha")                private LocalDate fecha;
    @Column("valor")                private BigDecimal valor;
    @Column("estado")               private String estado;
    @Column("observaciones")        private String observaciones;
    @Column("activo")               private Boolean activo;
    @Column("created_at")           private LocalDateTime created_at;
    @Column("updated_at")           private LocalDateTime updated_at;
    @Column("deleted_at")           private LocalDateTime deleted_at;
    @Column("usuario_creacion")     private Long usuario_creacion;
    @Column("usuario_modificacion") private Long usuario_modificacion;
}
