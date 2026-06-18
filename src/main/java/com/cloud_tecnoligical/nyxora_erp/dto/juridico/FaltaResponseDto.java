package com.cloud_tecnoligical.nyxora_erp.dto.juridico;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FaltaResponseDto {
    private Long id;
    private Long clasificacionFaltaId;
    private String codigo;
    private String nombre;
    private String descripcion;
    private Integer caducidadDias;
    private String politica;
    private Boolean active;
    private LocalDateTime createdAt;
}
