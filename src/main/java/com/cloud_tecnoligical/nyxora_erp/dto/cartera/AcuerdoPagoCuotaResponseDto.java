package com.cloud_tecnoligical.nyxora_erp.dto.cartera;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AcuerdoPagoCuotaResponseDto {
    private Long id;
    private Long acuerdoPagoId;
    private Integer numeroCuota;
    private BigDecimal valor;
    private LocalDate fechaAplicacion;
    private String estado;
}
