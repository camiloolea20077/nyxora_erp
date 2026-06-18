package com.cloud_tecnoligical.nyxora_erp.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Getter;
import lombok.Setter;

/** Proceso disciplinario (R2DBC). */
@Table("proceso_disciplinario")
@Getter
@Setter
public class ProcesoDisciplinarioEntity {

    @Id
    private Long id;

    @Column("empresa_id")           private Long empresa_id;
    @Column("fecha")                private LocalDate fecha;
    @Column("vinculacion_id")       private Long vinculacion_id;
    @Column("responsable_id")       private Long responsable_id;
    @Column("descripcion")          private String descripcion;
    @Column("estado")               private String estado;
    @Column("activo")               private Boolean activo;
    @Column("created_at")           private LocalDateTime created_at;
    @Column("updated_at")           private LocalDateTime updated_at;
    @Column("deleted_at")           private LocalDateTime deleted_at;
    @Column("usuario_creacion")     private Long usuario_creacion;
    @Column("usuario_modificacion") private Long usuario_modificacion;
}
