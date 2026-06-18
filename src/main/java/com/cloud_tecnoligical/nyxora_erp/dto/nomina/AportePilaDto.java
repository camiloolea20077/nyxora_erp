package com.cloud_tecnoligical.nyxora_erp.dto.nomina;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AportePilaDto {
    private Long id;
    private Long empleadoId;
    private String empleadoNombre;
    private String tipoAporte;
    private BigDecimal ibc;
    private BigDecimal valorEmpleado;
    private BigDecimal valorPatrono;
}
