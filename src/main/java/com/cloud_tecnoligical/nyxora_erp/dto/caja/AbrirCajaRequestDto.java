package com.cloud_tecnoligical.nyxora_erp.dto.caja;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

/** Apertura de caja: base inicial en efectivo. */
@Getter
@Setter
public class AbrirCajaRequestDto {
    private BigDecimal saldoInicial;
}
