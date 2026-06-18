package com.cloud_tecnoligical.nyxora_erp.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Getter;
import lombok.Setter;

/** Falta imputada en un proceso disciplinario (R2DBC). */
@Table("proceso_falta")
@Getter
@Setter
public class ProcesoFaltaEntity {

    @Id
    private Long id;

    @Column("proceso_disciplinario_id") private Long proceso_disciplinario_id;
    @Column("falta_id")                 private Long falta_id;
    @Column("created_at")               private LocalDateTime created_at;
    @Column("deleted_at")               private LocalDateTime deleted_at;
}
