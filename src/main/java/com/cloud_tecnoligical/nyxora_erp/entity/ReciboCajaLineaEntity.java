package com.cloud_tecnoligical.nyxora_erp.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Getter;
import lombok.Setter;

/** Aplicación de un recibo de caja a una cuenta por cobrar (R2DBC). */
@Table("recibo_caja_linea")
@Getter
@Setter
public class ReciboCajaLineaEntity {

    @Id
    private Long id;

    @Column("recibo_caja_id")       private Long recibo_caja_id;
    @Column("cuenta_por_cobrar_id") private Long cuenta_por_cobrar_id;
    @Column("valor_aplicado")       private BigDecimal valor_aplicado;
    @Column("created_at")           private LocalDateTime created_at;
    @Column("deleted_at")           private LocalDateTime deleted_at;
}
