package com.cloud_tecnoligical.nyxora_erp.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Getter;
import lombok.Setter;

/** Resolución de facturación DIAN (R2DBC). Numeración consecutiva de facturas (rango + prefijo). */
@Table("resolucion_dian")
@Getter
@Setter
public class ResolucionDianEntity {

    @Id
    private Long id;

    @Column("empresa_id")           private Long empresa_id;
    @Column("numero_resolucion")    private String numero_resolucion;
    @Column("prefijo")              private String prefijo;
    @Column("factura_inicial")      private Long factura_inicial;
    @Column("factura_final")        private Long factura_final;
    @Column("fecha_inicial")        private LocalDate fecha_inicial;
    @Column("fecha_final")          private LocalDate fecha_final;
    @Column("clave_tecnica")        private String clave_tecnica;
    @Column("descripcion")          private String descripcion;
    @Column("consecutivo_actual")   private Long consecutivo_actual;
    @Column("activo")               private Boolean activo;
    @Column("created_at")           private LocalDateTime created_at;
    @Column("updated_at")           private LocalDateTime updated_at;
    @Column("deleted_at")           private LocalDateTime deleted_at;
    @Column("usuario_creacion")     private Long usuario_creacion;
    @Column("usuario_modificacion") private Long usuario_modificacion;
}
