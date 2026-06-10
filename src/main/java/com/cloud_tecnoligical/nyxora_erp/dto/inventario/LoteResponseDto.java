package com.cloud_tecnoligical.nyxora_erp.dto.inventario;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoteResponseDto {
    private Long id;
    private Long productoVarianteId;
    private String codigo;
    private String nombre;
    private LocalDate fechaFabricado;
    private LocalDate fechaVencimiento;
    private Boolean active;
    private LocalDateTime createdAt;
}
