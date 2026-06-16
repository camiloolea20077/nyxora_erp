package com.cloud_tecnoligical.nyxora_erp.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Getter;
import lombok.Setter;

/** Evento RADIAN de una factura de proveedor (R2DBC). */
@Table("factura_proveedor_evento")
@Getter
@Setter
public class FacturaProveedorEventoEntity {

    @Id
    private Long id;

    @Column("factura_proveedor_id") private Long factura_proveedor_id;
    @Column("evento")               private String evento;
    @Column("fecha_evento")         private LocalDate fecha_evento;
    @Column("cude_evento")          private String cude_evento;
    @Column("concepto_reclamo")     private String concepto_reclamo;
    @Column("descripcion_reclamo")  private String descripcion_reclamo;
    @Column("estado")               private String estado;
    @Column("error_evento")         private String error_evento;
    @Column("created_at")           private LocalDateTime created_at;
    @Column("deleted_at")           private LocalDateTime deleted_at;
}
