package com.cloud_tecnoligical.nyxora_erp.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Getter;
import lombok.Setter;

/** Punto de recaudo (R2DBC). Estados: cerrada ↔ abierta. */
@Table("caja")
@Getter
@Setter
public class CajaEntity {

    @Id
    private Long id;

    @Column("empresa_id")           private Long empresa_id;
    @Column("sede_id")              private Long sede_id;
    @Column("usuario_id")           private Long usuario_id;
    @Column("codigo")               private String codigo;
    @Column("nombre")               private String nombre;
    @Column("estado")               private String estado;
    @Column("saldo_inicial")        private BigDecimal saldo_inicial;
    @Column("fecha_apertura")       private LocalDateTime fecha_apertura;
    @Column("fecha_cierre")         private LocalDateTime fecha_cierre;
    @Column("activo")               private Boolean activo;
    @Column("created_at")           private LocalDateTime created_at;
    @Column("updated_at")           private LocalDateTime updated_at;
    @Column("deleted_at")           private LocalDateTime deleted_at;
    @Column("usuario_creacion")     private Long usuario_creacion;
    @Column("usuario_modificacion") private Long usuario_modificacion;
}
