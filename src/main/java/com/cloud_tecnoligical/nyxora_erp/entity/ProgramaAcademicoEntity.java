package com.cloud_tecnoligical.nyxora_erp.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Getter;
import lombok.Setter;

/** Programa académico (R2DBC). */
@Table("programa_academico")
@Getter
@Setter
public class ProgramaAcademicoEntity {

    @Id
    private Long id;

    @Column("empresa_id")               private Long empresa_id;
    @Column("codigo")                   private String codigo;
    @Column("nombre")                   private String nombre;
    @Column("tipo_programa")            private String tipo_programa;
    @Column("modalidad")                private String modalidad;
    @Column("centro_costo_programa_id") private Long centro_costo_programa_id;
    @Column("centro_costo_facultad_id") private Long centro_costo_facultad_id;
    @Column("registro_academico")       private String registro_academico;
    @Column("descripcion")              private String descripcion;
    @Column("activo")                   private Boolean activo;
    @Column("created_at")               private LocalDateTime created_at;
    @Column("updated_at")               private LocalDateTime updated_at;
    @Column("deleted_at")               private LocalDateTime deleted_at;
}
