package com.cloud_tecnoligical.nyxora_erp.dto.academico;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AsignaturaResponseDto {
    private Long id;
    private String codigo;
    private String nombre;
    private String descripcion;
    private Long centroCostoDepartamentoId;
    private Boolean active;
    private LocalDateTime createdAt;
}
