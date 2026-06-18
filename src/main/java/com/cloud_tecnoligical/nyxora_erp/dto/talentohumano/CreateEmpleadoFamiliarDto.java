package com.cloud_tecnoligical.nyxora_erp.dto.talentohumano;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateEmpleadoFamiliarDto {
    @NotBlank(message = "El nombre del familiar es obligatorio")
    private String nombreApellido;
    private LocalDate fechaNacimiento;
    private String parentesco;
    private Boolean aCargo;
    private Boolean vivo;
    private Boolean convive;
    private Boolean dependienteRetencion;
}
