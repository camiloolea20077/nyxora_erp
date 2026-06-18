package com.cloud_tecnoligical.nyxora_erp.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Getter;
import lombok.Setter;

/** Descargo de un proceso disciplinario (R2DBC). */
@Table("proceso_descargo")
@Getter
@Setter
public class ProcesoDescargoEntity {

    @Id
    private Long id;

    @Column("proceso_disciplinario_id") private Long proceso_disciplinario_id;
    @Column("fecha")                    private LocalDate fecha;
    @Column("texto")                    private String texto;
    @Column("created_at")               private LocalDateTime created_at;
    @Column("deleted_at")               private LocalDateTime deleted_at;
}
