package com.cloud_tecnoligical.nyxora_erp.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Getter;
import lombok.Setter;

/** Estudio del empleado (R2DBC). El empleado es un tercero (rol empleado). */
@Table("empleado_estudio")
@Getter
@Setter
public class EmpleadoEstudioEntity {

    @Id
    private Long id;

    @Column("empresa_id")                 private Long empresa_id;
    @Column("empleado_id")                private Long empleado_id;
    @Column("nivel_estudio_id")           private Long nivel_estudio_id;
    @Column("institucion")                private String institucion;
    @Column("titulo")                     private String titulo;
    @Column("fecha_inicial")              private LocalDate fecha_inicial;
    @Column("fecha_final")                private LocalDate fecha_final;
    @Column("fecha_grado")                private LocalDate fecha_grado;
    @Column("numero_tarjeta_profesional") private String numero_tarjeta_profesional;
    @Column("municipio_estudio_id")       private Long municipio_estudio_id;
    @Column("semestres_aprobados")        private Short semestres_aprobados;
    @Column("convalidado")                private Boolean convalidado;
    @Column("activo")                     private Boolean activo;
    @Column("created_at")                 private LocalDateTime created_at;
    @Column("updated_at")                 private LocalDateTime updated_at;
    @Column("deleted_at")                 private LocalDateTime deleted_at;
}
