package com.cloud_tecnoligical.nyxora_erp.dto.nomina;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LiquidacionDetalleDto {
    private Long id;
    private Long empleadoId;
    private String empleadoNombre;
    private Long conceptoNominaId;
    private String conceptoNombre;
    private String clase;
    private BigDecimal base;
    private BigDecimal cantidad;
    private BigDecimal valor;
    private BigDecimal valorEmpleado;
    private BigDecimal valorPatrono;
    private String tipoAporte;
}
