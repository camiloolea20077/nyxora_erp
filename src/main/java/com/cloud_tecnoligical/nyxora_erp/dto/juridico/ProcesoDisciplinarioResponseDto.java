package com.cloud_tecnoligical.nyxora_erp.dto.juridico;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProcesoDisciplinarioResponseDto {
    private Long id;
    private LocalDate fecha;
    private Long vinculacionId;
    private String investigadoNombre;
    private Long responsableId;
    private String descripcion;
    private String estado;
    private Boolean active;
    private LocalDateTime createdAt;
}
