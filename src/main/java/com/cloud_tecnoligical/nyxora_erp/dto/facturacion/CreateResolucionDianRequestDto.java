package com.cloud_tecnoligical.nyxora_erp.dto.facturacion;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateResolucionDianRequestDto {

    @NotBlank(message = "El número de resolución es obligatorio")
    private String numeroResolucion;

    private String prefijo;
    private Long facturaInicial;
    private Long facturaFinal;
    private LocalDate fechaInicial;
    private LocalDate fechaFinal;
    private String claveTecnica;
    private String descripcion;
}
