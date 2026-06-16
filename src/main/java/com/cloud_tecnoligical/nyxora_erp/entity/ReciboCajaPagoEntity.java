package com.cloud_tecnoligical.nyxora_erp.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Getter;
import lombok.Setter;

/** Medio de pago de un recibo de caja (R2DBC). */
@Table("recibo_caja_pago")
@Getter
@Setter
public class ReciboCajaPagoEntity {

    @Id
    private Long id;

    @Column("recibo_caja_id")  private Long recibo_caja_id;
    @Column("forma_pago_id")   private Long forma_pago_id;
    @Column("valor")           private BigDecimal valor;
    @Column("banco_id")        private Long banco_id;
    @Column("numero_cheque")   private String numero_cheque;
    @Column("numero_tarjeta")  private String numero_tarjeta;
    @Column("cuenta_bancaria") private String cuenta_bancaria;
    @Column("created_at")      private LocalDateTime created_at;
    @Column("deleted_at")      private LocalDateTime deleted_at;
}
