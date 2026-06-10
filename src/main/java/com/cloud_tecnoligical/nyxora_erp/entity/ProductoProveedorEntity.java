package com.cloud_tecnoligical.nyxora_erp.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Getter;
import lombok.Setter;

/** Proveedor de un producto (R2DBC, ← com_productos_proveedores). */
@Table("producto_proveedor")
@Getter
@Setter
public class ProductoProveedorEntity {

    @Id
    private Long id;

    @Column("producto_id")     private Long producto_id;
    @Column("proveedor_id")    private Long proveedor_id;
    @Column("codigo_producto") private String codigo_producto;
    @Column("cantidad_minima") private BigDecimal cantidad_minima;
    @Column("plazo_entrega")   private Integer plazo_entrega;
    @Column("activo")          private Boolean activo;
    @Column("created_at")      private LocalDateTime created_at;
    @Column("updated_at")      private LocalDateTime updated_at;
    @Column("deleted_at")      private LocalDateTime deleted_at;
}
