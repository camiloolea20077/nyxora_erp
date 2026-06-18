package com.cloud_tecnoligical.nyxora_erp.dto.nomina;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NovedadNominaTableDto {
    private Long id;
    private Long vinculacionId;
    private String empleadoNombre;
    private String conceptoNombre;
    private BigDecimal cantidadValor;
    private String estadoNovedad;
    private Boolean anulado;
}
