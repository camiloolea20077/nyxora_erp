package com.cloud_tecnoligical.nyxora_erp.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Getter;
import lombok.Setter;

/** Factura electrónica recibida de un proveedor (R2DBC). Estados libres: recibida, aceptada, rechazada. */
@Table("factura_proveedor")
@Getter
@Setter
public class FacturaProveedorEntity {

    @Id
    private Long id;

    @Column("empresa_id")           private Long empresa_id;
    @Column("proveedor_id")         private Long proveedor_id;
    @Column("receptor_id")          private Long receptor_id;
    @Column("numero_documento")     private String numero_documento;
    @Column("cufe")                 private String cufe;
    @Column("fecha_recepcion")      private LocalDate fecha_recepcion;
    @Column("valor_factura")        private BigDecimal valor_factura;
    @Column("email_remitente")      private String email_remitente;
    @Column("xml_factura")          private String xml_factura;
    @Column("pdf_url")              private String pdf_url;
    @Column("estado")               private String estado;
    @Column("activo")               private Boolean activo;
    @Column("created_at")           private LocalDateTime created_at;
    @Column("updated_at")           private LocalDateTime updated_at;
    @Column("deleted_at")           private LocalDateTime deleted_at;
    @Column("usuario_creacion")     private Long usuario_creacion;
    @Column("usuario_modificacion") private Long usuario_modificacion;
}
