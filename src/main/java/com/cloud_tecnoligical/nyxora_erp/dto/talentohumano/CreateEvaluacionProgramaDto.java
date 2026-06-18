package com.cloud_tecnoligical.nyxora_erp.dto.talentohumano;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateEvaluacionProgramaDto {
    private String codigo;

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    private LocalDate fechaInicial;
    private LocalDate fechaFinal;
}
