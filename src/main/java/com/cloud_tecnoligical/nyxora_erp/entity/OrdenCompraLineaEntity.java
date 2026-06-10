package com.cloud_tecnoligical.nyxora_erp.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Getter;
import lombok.Setter;

/** Línea de orden de compra (R2DBC). */
@Table("orden_compra_linea")
@Getter
@Setter
public class OrdenCompraLineaEntity {

    @Id
    private Long id;

    @Column("orden_compra_id")      private Long orden_compra_id;
    @Column("producto_id")          private Long producto_id;
    @Column("producto_variante_id") private Long producto_variante_id;
    @Column("descripcion")          private String descripcion;
    @Column("cantidad")             private BigDecimal cantidad;
    @Column("unidad_medida_id")     private Long unidad_medida_id;
    @Column("valor_unitario")       private BigDecimal valor_unitario;
    @Column("descuento_porcentaje") private BigDecimal descuento_porcentaje;
    @Column("descuento_valor")      private BigDecimal descuento_valor;
    @Column("impuesto_id")          private Long impuesto_id;
    @Column("impuesto_porcentaje")  private BigDecimal impuesto_porcentaje;
    @Column("impuesto_valor")       private BigDecimal impuesto_valor;
    @Column("subtotal")             private BigDecimal subtotal;
    @Column("total")                private BigDecimal total;
    @Column("cantidad_recibida")    private BigDecimal cantidad_recibida;
    @Column("cantidad_pendiente")   private BigDecimal cantidad_pendiente;
    @Column("centro_costo_id")      private Long centro_costo_id;
    @Column("created_at")           private LocalDateTime created_at;
    @Column("updated_at")           private LocalDateTime updated_at;
    @Column("deleted_at")           private LocalDateTime deleted_at;
}
