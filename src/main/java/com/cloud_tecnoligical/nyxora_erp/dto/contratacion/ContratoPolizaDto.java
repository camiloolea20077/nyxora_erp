package com.cloud_tecnoligical.nyxora_erp.dto.contratacion;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ContratoPolizaDto {
    private Long id;
    private Long polizaSeguroId;
    private String numero;
    private String tipo;
    private BigDecimal valorAsegurado;
}
