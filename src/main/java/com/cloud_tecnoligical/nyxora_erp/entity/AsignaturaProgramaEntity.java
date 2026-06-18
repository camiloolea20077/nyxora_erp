package com.cloud_tecnoligical.nyxora_erp.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Getter;
import lombok.Setter;

/** Enlace asignatura ↔ programa (semestre/créditos) (R2DBC). */
@Table("asignatura_programa")
@Getter
@Setter
public class AsignaturaProgramaEntity {

    @Id
    private Long id;

    @Column("asignatura_id")         private Long asignatura_id;
    @Column("programa_academico_id") private Long programa_academico_id;
    @Column("semestre")              private Integer semestre;
    @Column("creditos")              private Integer creditos;
    @Column("activo")                private Boolean activo;
    @Column("created_at")            private LocalDateTime created_at;
    @Column("deleted_at")            private LocalDateTime deleted_at;
}
