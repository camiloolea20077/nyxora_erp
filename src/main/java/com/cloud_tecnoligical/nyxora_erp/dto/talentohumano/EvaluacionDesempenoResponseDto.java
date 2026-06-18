package com.cloud_tecnoligical.nyxora_erp.dto.talentohumano;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EvaluacionDesempenoResponseDto {
    private Long id;
    private Long evaluacionProgramaId;
    private Long empleadoId;
    private String tipoEvaluacion;
    private LocalDateTime fechaInicial;
    private LocalDateTime fechaFinal;
    private BigDecimal calificacion;
    private LocalDateTime createdAt;
}
