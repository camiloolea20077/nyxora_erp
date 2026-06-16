package com.cloud_tecnoligical.nyxora_erp.dto.cuentaspagar;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateObligacionPagoRetencionDto {

    @NotNull(message = "El impuesto de la retención es obligatorio")
    private Long impuestoId;

    private BigDecimal base;
    private String limite;

    @NotNull(message = "El valor de la retención es obligatorio")
    private BigDecimal valor;
}
