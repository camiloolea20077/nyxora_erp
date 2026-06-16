package com.cloud_tecnoligical.nyxora_erp.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Getter;
import lombok.Setter;

/** Vínculo activo fijo ↔ póliza de seguro (R2DBC). */
@Table("activo_fijo_poliza")
@Getter
@Setter
public class ActivoFijoPolizaEntity {

    @Id
    private Long id;

    @Column("activo_fijo_id")   private Long activo_fijo_id;
    @Column("poliza_seguro_id") private Long poliza_seguro_id;
    @Column("created_at")       private LocalDateTime created_at;
    @Column("deleted_at")       private LocalDateTime deleted_at;
}
