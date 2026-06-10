package com.cloud_tecnoligical.nyxora_erp.dto.contabilidad;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SaldoContableResponseDto {
    private Long id;
    private Long periodoContableId;
    private Long cuentaId;
    private Long terceroId;
    private Long centroCostoId;
    private BigDecimal saldoDebitoAnterior;
    private BigDecimal saldoCreditoAnterior;
    private BigDecimal debitoPeriodo;
    private BigDecimal creditoPeriodo;
    private BigDecimal saldoDebitoFinal;
    private BigDecimal saldoCreditoFinal;
}
