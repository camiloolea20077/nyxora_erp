package com.cloud_tecnoligical.nyxora_erp.dto.contratacion;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ModalidadContratoResponseDto {
    private Long id;
    private String codigo;
    private String nombre;
    private String descripcion;
    private Boolean active;
    private LocalDateTime createdAt;
}
