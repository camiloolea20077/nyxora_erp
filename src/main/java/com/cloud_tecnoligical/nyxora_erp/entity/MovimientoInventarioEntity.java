package com.cloud_tecnoligical.nyxora_erp.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Getter;
import lombok.Setter;

/**
 * Movimiento de inventario inmutable (R2DBC, ← inv_detalles_inventarios). APPEND-ONLY.
 * La cantidad se almacena CON SIGNO (positivo suma, negativo resta); el tipo es descriptivo.
 */
@Table("movimiento_inventario")
@Getter
@Setter
public class MovimientoInventarioEntity {

    @Id
    private Long id;

    @Column("empresa_id")           private Long empresa_id;
    @Column("bodega_id")            private Long bodega_id;
    @Column("ubicacion_id")         private Long ubicacion_id;
    @Column("producto_id")          private Long producto_id;
    @Column("producto_variante_id") private Long producto_variante_id;
    @Column("lote_id")              private Long lote_id;
    @Column("tipo")                 private String tipo;            // entrada | salida | ajuste | traslado
    @Column("fecha")                private LocalDate fecha;
    @Column("cantidad")             private BigDecimal cantidad;
    @Column("costo_unitario")       private BigDecimal costo_unitario;
    @Column("descuento_porcentaje") private BigDecimal descuento_porcentaje;
    @Column("descuento_valor")      private BigDecimal descuento_valor;
    @Column("impuesto_id")          private Long impuesto_id;
    @Column("impuesto_porcentaje")  private BigDecimal impuesto_porcentaje;
    @Column("impuesto_valor")       private BigDecimal impuesto_valor;
    @Column("subtotal")             private BigDecimal subtotal;
    @Column("total")                private BigDecimal total;
    @Column("centro_costo_id")      private Long centro_costo_id;
    @Column("tercero_id")           private Long tercero_id;
    @Column("descripcion")          private String descripcion;
    @Column("origen_modulo")        private String origen_modulo;
    @Column("origen_id")            private Long origen_id;
    @Column("created_at")           private LocalDateTime created_at;
    @Column("usuario_creacion")     private Long usuario_creacion;
}
