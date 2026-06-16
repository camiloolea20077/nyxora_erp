package com.cloud_tecnoligical.nyxora_erp.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Getter;
import lombok.Setter;

/** Vínculo activo fijo ↔ tercero responsable (R2DBC). */
@Table("activo_fijo_responsable")
@Getter
@Setter
public class ActivoFijoResponsableEntity {

    @Id
    private Long id;

    @Column("activo_fijo_id") private Long activo_fijo_id;
    @Column("tercero_id")     private Long tercero_id;
    @Column("activo")         private Boolean activo;
    @Column("created_at")     private LocalDateTime created_at;
    @Column("deleted_at")     private LocalDateTime deleted_at;
}
