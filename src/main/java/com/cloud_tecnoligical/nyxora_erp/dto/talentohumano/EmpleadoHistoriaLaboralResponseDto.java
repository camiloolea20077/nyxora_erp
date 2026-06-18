package com.cloud_tecnoligical.nyxora_erp.dto.talentohumano;

import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmpleadoHistoriaLaboralResponseDto {
    private Long id;
    private Long empleadoId;
    private String nombreEmpresa;
    private String cargo;
    private String tipoContrato;
    private LocalDate fechaInicio;
    private LocalDate fechaFinal;
    private String jefeInmediato;
    private Long municipioId;
    private Boolean esPublico;
}
