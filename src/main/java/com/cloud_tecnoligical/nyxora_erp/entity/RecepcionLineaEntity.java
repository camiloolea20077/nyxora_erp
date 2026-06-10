package com.cloud_tecnoligical.nyxora_erp.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Getter;
import lombok.Setter;

/** Línea de recepción (R2DBC). */
@Table("recepcion_linea")
@Getter
@Setter
public class RecepcionLineaEntity {

    @Id
    private Long id;

    @Column("recepcion_id")          private Long recepcion_id;
    @Column("orden_compra_linea_id") private Long orden_compra_linea_id;
    @Column("producto_id")           private Long producto_id;
    @Column("producto_variante_id")  private Long producto_variante_id;
    @Column("lote_id")               private Long lote_id;
    @Column("ubicacion_id")          private Long ubicacion_id;
    @Column("cantidad_recibida")     private BigDecimal cantidad_recibida;
    @Column("costo_unitario")        private BigDecimal costo_unitario;
    @Column("created_at")            private LocalDateTime created_at;
    @Column("deleted_at")            private LocalDateTime deleted_at;
}
