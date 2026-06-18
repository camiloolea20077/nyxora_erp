package com.cloud_tecnoligical.nyxora_erp.dto.nomina;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GrupoNominaResponseDto {
    private Long id;
    private String codigo;
    private String nombre;
    private String frecuenciaPago;
    private Boolean active;
    private LocalDateTime createdAt;
}
