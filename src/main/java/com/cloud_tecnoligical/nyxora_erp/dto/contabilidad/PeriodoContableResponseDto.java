package com.cloud_tecnoligical.nyxora_erp.dto.contabilidad;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PeriodoContableResponseDto {
    private Long id;
    private Long vigenciaId;
    private Integer anio;
    private Integer mes;
    private String estado;
    private LocalDateTime fechaCierre;
    private LocalDateTime createdAt;
}
