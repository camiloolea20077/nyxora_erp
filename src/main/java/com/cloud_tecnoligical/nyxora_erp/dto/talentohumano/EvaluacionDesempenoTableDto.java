package com.cloud_tecnoligical.nyxora_erp.dto.talentohumano;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EvaluacionDesempenoTableDto {
    private Long id;
    private Long evaluacionProgramaId;
    private Long empleadoId;
    private String tipoEvaluacion;
    private LocalDateTime fechaInicial;
    private BigDecimal calificacion;
}
