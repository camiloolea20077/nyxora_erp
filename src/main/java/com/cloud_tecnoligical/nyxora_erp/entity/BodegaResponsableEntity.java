package com.cloud_tecnoligical.nyxora_erp.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Getter;
import lombok.Setter;

/** Responsable de una bodega (R2DBC, ← inv_bodegas_responsables). */
@Table("bodega_responsable")
@Getter
@Setter
public class BodegaResponsableEntity {

    @Id
    private Long id;

    @Column("bodega_id")     private Long bodega_id;
    @Column("tercero_id")    private Long tercero_id;
    @Column("predeterminado") private Boolean predeterminado;
    @Column("activo")        private Boolean activo;
    @Column("created_at")    private LocalDateTime created_at;
    @Column("deleted_at")    private LocalDateTime deleted_at;
}
