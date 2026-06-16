package com.cloud_tecnoligical.nyxora_erp.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Getter;
import lombok.Setter;

/** Encabezado de factura de venta (R2DBC). Estados: borrador → emitida → anulada. */
@Table("factura")
@Getter
@Setter
public class FacturaEntity {

    @Id
    private Long id;

    @Column("empresa_id")           private Long empresa_id;
    @Column("sede_id")              private Long sede_id;
    @Column("vigencia_id")          private Long vigencia_id;
    @Column("tipo_documento_id")    private Long tipo_documento_id;
    @Column("resolucion_dian_id")   private Long resolucion_dian_id;
    @Column("numero")               private String numero;
    @Column("cliente_id")           private Long cliente_id;
    @Column("bodega_id")            private Long bodega_id;
    @Column("centro_costo_id")      private Long centro_costo_id;
    @Column("condicion_pago_id")    private Long condicion_pago_id;
    @Column("fecha")                private LocalDate fecha;
    @Column("fecha_vencimiento")    private LocalDate fecha_vencimiento;
    @Column("observaciones")        private String observaciones;
    @Column("estado")               private String estado;
    @Column("subtotal")             private BigDecimal subtotal;
    @Column("descuento")            private BigDecimal descuento;
    @Column("impuestos")            private BigDecimal impuestos;
    @Column("total")                private BigDecimal total;
    @Column("activo")               private Boolean activo;
    @Column("created_at")           private LocalDateTime created_at;
    @Column("updated_at")           private LocalDateTime updated_at;
    @Column("deleted_at")           private LocalDateTime deleted_at;
    @Column("usuario_creacion")     private Long usuario_creacion;
    @Column("usuario_modificacion") private Long usuario_modificacion;
}
