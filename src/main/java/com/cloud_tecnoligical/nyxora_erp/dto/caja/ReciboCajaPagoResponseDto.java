package com.cloud_tecnoligical.nyxora_erp.dto.caja;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReciboCajaPagoResponseDto {
    private Long id;
    private Long reciboCajaId;
    private Long formaPagoId;
    private BigDecimal valor;
    private Long bancoId;
    private String numeroCheque;
    private String numeroTarjeta;
    private String cuentaBancaria;
}
