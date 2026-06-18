package com.cloud_tecnoligical.nyxora_erp.dto.nomina;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VinculacionTableDto {
    private Long id;
    private String codigo;
    private Long empleadoId;
    private String empleadoNombre;
    private String cargoNombre;
    private BigDecimal sueldo;
    private LocalDate fecha;
    private String estadoVinculacion;
    private Boolean active;
}
