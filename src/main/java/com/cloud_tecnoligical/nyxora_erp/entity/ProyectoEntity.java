package com.cloud_tecnoligical.nyxora_erp.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Getter;
import lombok.Setter;

/** Proyecto (R2DBC, ← com_proyectos). */
@Table("proyecto")
@Getter
@Setter
public class ProyectoEntity {

    @Id
    private Long id;

    @Column("empresa_id")           private Long empresa_id;
    @Column("codigo")               private String codigo;
    @Column("nombre")               private String nombre;
    @Column("descripcion")          private String descripcion;
    @Column("programa_id")          private Long programa_id;
    @Column("fecha_inicio")         private LocalDate fecha_inicio;
    @Column("fecha_final")          private LocalDate fecha_final;
    @Column("activo")               private Boolean activo;
    @Column("created_at")           private LocalDateTime created_at;
    @Column("updated_at")           private LocalDateTime updated_at;
    @Column("deleted_at")           private LocalDateTime deleted_at;
    @Column("usuario_creacion")     private Long usuario_creacion;
    @Column("usuario_modificacion") private Long usuario_modificacion;
}
