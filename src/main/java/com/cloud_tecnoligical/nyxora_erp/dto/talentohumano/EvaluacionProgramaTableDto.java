package com.cloud_tecnoligical.nyxora_erp.dto.talentohumano;

import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EvaluacionProgramaTableDto {
    private Long id;
    private String codigo;
    private String nombre;
    private LocalDate fechaInicial;
    private LocalDate fechaFinal;
    private Boolean active;
}
