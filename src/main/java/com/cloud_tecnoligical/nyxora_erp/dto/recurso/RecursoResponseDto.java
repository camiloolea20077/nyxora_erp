package com.cloud_tecnoligical.nyxora_erp.dto.recurso;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RecursoResponseDto {
    private Long id;
    private String codigo;
    private String nombre;
    private String tipoRecurso;
    private String driver;
    private Boolean costoAdicional;
    private String descripcion;
    private Boolean active;
    private LocalDateTime createdAt;
}
