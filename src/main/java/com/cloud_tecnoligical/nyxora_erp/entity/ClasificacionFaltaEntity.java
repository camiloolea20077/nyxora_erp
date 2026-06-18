package com.cloud_tecnoligical.nyxora_erp.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Getter;
import lombok.Setter;

/** Clasificación de falta (R2DBC). */
@Table("clasificacion_falta")
@Getter
@Setter
public class ClasificacionFaltaEntity {

    @Id
    private Long id;

    @Column("empresa_id") private Long empresa_id;
    @Column("codigo")     private String codigo;
    @Column("nombre")     private String nombre;
    @Column("activo")     private Boolean activo;
    @Column("created_at") private LocalDateTime created_at;
    @Column("deleted_at") private LocalDateTime deleted_at;
}
