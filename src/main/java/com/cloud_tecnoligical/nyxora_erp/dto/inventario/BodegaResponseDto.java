package com.cloud_tecnoligical.nyxora_erp.dto.inventario;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BodegaResponseDto {
    private Long id;
    private Long sedeId;
    private Long centroCostoId;
    private String codigo;
    private String nombre;
    private String tipoAbastecimiento;
    private String direccion;
    private String latitud;
    private String longitud;
    private Boolean permiteCompra;
    private Boolean active;
    private LocalDateTime createdAt;
}
