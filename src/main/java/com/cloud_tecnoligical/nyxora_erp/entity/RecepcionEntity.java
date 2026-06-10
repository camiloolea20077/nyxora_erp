package com.cloud_tecnoligical.nyxora_erp.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Getter;
import lombok.Setter;

/** Recepción de mercancía contra una orden de compra (R2DBC). Estados: borrador|confirmada|anulada. */
@Table("recepcion")
@Getter
@Setter
public class RecepcionEntity {

    @Id
    private Long id;

    @Column("empresa_id")           private Long empresa_id;
    @Column("orden_compra_id")      private Long orden_compra_id;
    @Column("bodega_id")            private Long bodega_id;
    @Column("tipo_documento_id")    private Long tipo_documento_id;
    @Column("numero")               private String numero;
    @Column("fecha")                private LocalDate fecha;
    @Column("estado")               private String estado;
    @Column("observaciones")        private String observaciones;
    @Column("activo")               private Boolean activo;
    @Column("created_at")           private LocalDateTime created_at;
    @Column("updated_at")           private LocalDateTime updated_at;
    @Column("deleted_at")           private LocalDateTime deleted_at;
    @Column("usuario_creacion")     private Long usuario_creacion;
    @Column("usuario_modificacion") private Long usuario_modificacion;
}
