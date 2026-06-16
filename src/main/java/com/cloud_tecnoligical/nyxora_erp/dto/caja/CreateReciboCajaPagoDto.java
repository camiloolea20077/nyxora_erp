package com.cloud_tecnoligical.nyxora_erp.dto.caja;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateReciboCajaPagoDto {

    private Long formaPagoId;

    @NotNull(message = "El valor del pago es obligatorio")
    @Positive(message = "El valor del pago debe ser positivo")
    private BigDecimal valor;

    private Long bancoId;
    private String numeroCheque;
    private String numeroTarjeta;
    private String cuentaBancaria;
}
