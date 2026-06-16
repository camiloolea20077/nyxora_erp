package com.cloud_tecnoligical.nyxora_erp.dto.presupuesto;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FuenteFinanciamientoResponseDto {
    private Long id;
    private String codigo;
    private String nombre;
    private String descripcion;
    private String tipoRecurso;
    private Boolean active;
    private LocalDateTime createdAt;
}
