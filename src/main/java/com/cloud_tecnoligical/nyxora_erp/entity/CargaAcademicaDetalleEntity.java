package com.cloud_tecnoligical.nyxora_erp.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Getter;
import lombok.Setter;

/** Detalle de la carga académica (asignatura/grupo/horas) (R2DBC). */
@Table("carga_academica_detalle")
@Getter
@Setter
public class CargaAcademicaDetalleEntity {

    @Id
    private Long id;

    @Column("carga_academica_id")     private Long carga_academica_id;
    @Column("asignatura_programa_id") private Long asignatura_programa_id;
    @Column("grupo_academico_id")     private Long grupo_academico_id;
    @Column("horas")                  private BigDecimal horas;
    @Column("created_at")             private LocalDateTime created_at;
    @Column("deleted_at")             private LocalDateTime deleted_at;
}
