package com.cloud_tecnoligical.nyxora_erp.dto.organizacion;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProyectoResponseDto {
    private Long id;
    private String codigo;
    private String nombre;
    private String descripcion;
    private Long programaId;
    private LocalDate fechaInicio;
    private LocalDate fechaFinal;
    private Boolean active;
    private LocalDateTime createdAt;
}
