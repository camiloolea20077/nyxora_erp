package com.cloud_tecnoligical.nyxora_erp.dto.facturacion;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResolucionDianResponseDto {
    private Long id;
    private String numeroResolucion;
    private String prefijo;
    private Long facturaInicial;
    private Long facturaFinal;
    private LocalDate fechaInicial;
    private LocalDate fechaFinal;
    private String claveTecnica;
    private String descripcion;
    private Long consecutivoActual;
    private Boolean active;
    private LocalDateTime createdAt;
}
