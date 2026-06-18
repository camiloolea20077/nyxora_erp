package com.cloud_tecnoligical.nyxora_erp.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Getter;
import lombok.Setter;

/** Carga académica (docente) (R2DBC). */
@Table("carga_academica")
@Getter
@Setter
public class CargaAcademicaEntity {

    @Id
    private Long id;

    @Column("empresa_id")                 private Long empresa_id;
    @Column("vinculacion_id")             private Long vinculacion_id;
    @Column("nivel_estudio_id")           private Long nivel_estudio_id;
    @Column("numero_acto_administrativo") private String numero_acto_administrativo;
    @Column("fecha_acto_administrativo")  private LocalDate fecha_acto_administrativo;
    @Column("activo")                     private Boolean activo;
    @Column("created_at")                 private LocalDateTime created_at;
    @Column("updated_at")                 private LocalDateTime updated_at;
    @Column("deleted_at")                 private LocalDateTime deleted_at;
}
