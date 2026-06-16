package com.cloud_tecnoligical.nyxora_erp.dto.presupuesto;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CpcResponseDto {
    private Long id;
    private Long vigenciaId;
    private Long cpcPadreId;
    private String codigo;
    private String nombre;
    private Boolean manejaMovimiento;
    private Boolean active;
    private LocalDateTime createdAt;
}
