package com.cloud_tecnoligical.nyxora_erp.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Getter;
import lombok.Setter;

/** Línea de factura de venta (R2DBC). */
@Table("factura_linea")
@Getter
@Setter
public class FacturaLineaEntity {

    @Id
    private Long id;

    @Column("factura_id")           private Long factura_id;
    @Column("producto_id")          private Long producto_id;
    @Column("producto_variante_id") private Long producto_variante_id;
    @Column("descripcion")          private String descripcion;
    @Column("cantidad")             private BigDecimal cantidad;
    @Column("unidad_medida_id")     private Long unidad_medida_id;
    @Column("valor_unitario")       private BigDecimal valor_unitario;
    @Column("descuento_porcentaje") private BigDecimal descuento_porcentaje;
    @Column("descuento_valor")      private BigDecimal descuento_valor;
    @Column("subtotal")             private BigDecimal subtotal;
    @Column("impuesto_id")          private Long impuesto_id;
    @Column("porcentaje_impuesto")  private BigDecimal porcentaje_impuesto;
    @Column("valor_impuesto")       private BigDecimal valor_impuesto;
    @Column("discrimina_iva")       private Boolean discrimina_iva;
    @Column("total")                private BigDecimal total;
    @Column("bodega_id")            private Long bodega_id;
    @Column("lote_id")              private Long lote_id;
    @Column("centro_costo_id")      private Long centro_costo_id;
    @Column("created_at")           private LocalDateTime created_at;
    @Column("updated_at")           private LocalDateTime updated_at;
    @Column("deleted_at")           private LocalDateTime deleted_at;
}
