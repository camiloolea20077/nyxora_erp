package com.cloud_tecnoligical.nyxora_erp.dto.talentohumano;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateEvaluacionDesempenoDto {
    private Long evaluacionProgramaId;

    @NotNull(message = "El empleado es obligatorio")
    private Long empleadoId;

    private String tipoEvaluacion;
    private LocalDateTime fechaInicial;
    private LocalDateTime fechaFinal;
    private BigDecimal calificacion;
}
