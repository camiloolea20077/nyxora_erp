package com.cloud_tecnoligical.nyxora_erp.dto.juridico;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClasificacionFaltaResponseDto {
    private Long id;
    private String codigo;
    private String nombre;
    private Boolean active;
    private LocalDateTime createdAt;
}
