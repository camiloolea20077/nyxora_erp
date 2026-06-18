package com.cloud_tecnoligical.nyxora_erp.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Getter;
import lombok.Setter;

/** Falta disciplinaria (R2DBC). */
@Table("falta")
@Getter
@Setter
public class FaltaEntity {

    @Id
    private Long id;

    @Column("empresa_id")             private Long empresa_id;
    @Column("clasificacion_falta_id") private Long clasificacion_falta_id;
    @Column("codigo")                 private String codigo;
    @Column("nombre")                 private String nombre;
    @Column("descripcion")            private String descripcion;
    @Column("caducidad_dias")         private Integer caducidad_dias;
    @Column("politica")               private String politica;
    @Column("activo")                 private Boolean activo;
    @Column("created_at")             private LocalDateTime created_at;
    @Column("updated_at")             private LocalDateTime updated_at;
    @Column("deleted_at")             private LocalDateTime deleted_at;
}
