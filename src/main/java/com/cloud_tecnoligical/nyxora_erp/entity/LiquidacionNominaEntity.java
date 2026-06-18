package com.cloud_tecnoligical.nyxora_erp.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Getter;
import lombok.Setter;

/** Encabezado del proceso de liquidación de nómina de un periodo (R2DBC). */
@Table("liquidacion_nomina")
@Getter
@Setter
public class LiquidacionNominaEntity {

    @Id
    private Long id;

    @Column("empresa_id")           private Long empresa_id;
    @Column("grupo_nomina_id")      private Long grupo_nomina_id;
    @Column("anio")                 private Integer anio;
    @Column("mes")                  private Integer mes;
    @Column("periodo")              private String periodo;
    @Column("fecha")                private LocalDate fecha;
    @Column("estado")               private String estado;
    @Column("activo")               private Boolean activo;
    @Column("created_at")           private LocalDateTime created_at;
    @Column("updated_at")           private LocalDateTime updated_at;
    @Column("deleted_at")           private LocalDateTime deleted_at;
    @Column("usuario_creacion")     private Long usuario_creacion;
    @Column("usuario_modificacion") private Long usuario_modificacion;
}
