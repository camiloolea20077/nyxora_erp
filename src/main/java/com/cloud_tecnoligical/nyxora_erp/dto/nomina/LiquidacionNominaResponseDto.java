package com.cloud_tecnoligical.nyxora_erp.dto.nomina;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LiquidacionNominaResponseDto {
    private Long id;
    private Long grupoNominaId;
    private Integer anio;
    private Integer mes;
    private String periodo;
    private LocalDate fecha;
    private String estado;
    private Boolean active;
    private LocalDateTime createdAt;
}
