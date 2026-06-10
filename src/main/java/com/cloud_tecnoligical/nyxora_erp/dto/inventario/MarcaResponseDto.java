package com.cloud_tecnoligical.nyxora_erp.dto.inventario;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MarcaResponseDto {
    private Long id;
    private String codigo;
    private String nombre;
    private Boolean active;
    private LocalDateTime createdAt;
}
