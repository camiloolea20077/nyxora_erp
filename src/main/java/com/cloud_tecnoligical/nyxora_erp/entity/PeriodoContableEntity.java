package com.cloud_tecnoligical.nyxora_erp.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Getter;
import lombok.Setter;

/** Periodo contable (R2DBC). Máquina de estados de cierre: abierto/cerrado. Sin soft-delete. */
@Table("periodo_contable")
@Getter
@Setter
public class PeriodoContableEntity {

    @Id
    private Long id;

    @Column("empresa_id")           private Long empresa_id;
    @Column("vigencia_id")          private Long vigencia_id;
    @Column("anio")                 private Integer anio;
    @Column("mes")                  private Integer mes;
    @Column("estado")               private String estado;          // abierto | cerrado
    @Column("fecha_cierre")         private LocalDateTime fecha_cierre;
    @Column("created_at")           private LocalDateTime created_at;
    @Column("updated_at")           private LocalDateTime updated_at;
    @Column("usuario_creacion")     private Long usuario_creacion;
    @Column("usuario_modificacion") private Long usuario_modificacion;
}
