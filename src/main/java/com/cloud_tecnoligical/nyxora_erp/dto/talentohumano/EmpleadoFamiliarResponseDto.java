package com.cloud_tecnoligical.nyxora_erp.dto.talentohumano;

import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmpleadoFamiliarResponseDto {
    private Long id;
    private Long empleadoId;
    private String nombreApellido;
    private LocalDate fechaNacimiento;
    private String parentesco;
    private Boolean aCargo;
    private Boolean vivo;
    private Boolean convive;
    private Boolean dependienteRetencion;
}
