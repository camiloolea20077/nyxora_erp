package com.cloud_tecnoligical.nyxora_erp.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Getter;
import lombok.Setter;

/** Variante de producto (R2DBC, ← com_productos_variantes). La columna jsonb 'imagen' se omite. */
@Table("producto_variante")
@Getter
@Setter
public class ProductoVarianteEntity {

    @Id
    private Long id;

    @Column("producto_id")      private Long producto_id;
    @Column("sku_plu")          private String sku_plu;
    @Column("codigo_barra")     private String codigo_barra;
    @Column("precio_adicional") private BigDecimal precio_adicional;
    @Column("costo")            private BigDecimal costo;
    @Column("activo")           private Boolean activo;
    @Column("created_at")       private LocalDateTime created_at;
    @Column("updated_at")       private LocalDateTime updated_at;
    @Column("deleted_at")       private LocalDateTime deleted_at;
}
