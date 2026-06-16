package com.cloud_tecnoligical.nyxora_erp.dto.cuentaspagar;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ObligacionPagoRetencionResponseDto {
    private Long id;
    private Long obligacionPagoId;
    private Long impuestoId;
    private BigDecimal base;
    private String limite;
    private BigDecimal valor;
}
