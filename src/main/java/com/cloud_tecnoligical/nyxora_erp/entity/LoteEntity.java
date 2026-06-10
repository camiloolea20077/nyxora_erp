package com.cloud_tecnoligical.nyxora_erp.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Getter;
import lombok.Setter;

/** Lote (R2DBC, ← inv_lotes). */
@Table("lote")
@Getter
@Setter
public class LoteEntity {

    @Id
    private Long id;

    @Column("empresa_id")           private Long empresa_id;
    @Column("producto_variante_id") private Long producto_variante_id;
    @Column("codigo")               private String codigo;
    @Column("nombre")               private String nombre;
    @Column("fecha_fabricado")      private LocalDate fecha_fabricado;
    @Column("fecha_vencimiento")    private LocalDate fecha_vencimiento;
    @Column("activo")               private Boolean activo;
    @Column("created_at")           private LocalDateTime created_at;
    @Column("updated_at")           private LocalDateTime updated_at;
    @Column("deleted_at")           private LocalDateTime deleted_at;
}
