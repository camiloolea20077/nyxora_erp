package com.cloud_tecnoligical.nyxora_erp.dto.activosfijos;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PolizaSeguroResponseDto {
    private Long id;
    private String numero;
    private Long aseguradoraId;
    private String aseguradoraNombre;
    private String tipo;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private BigDecimal valorAsegurado;
    private Boolean active;
    private LocalDateTime createdAt;
}
