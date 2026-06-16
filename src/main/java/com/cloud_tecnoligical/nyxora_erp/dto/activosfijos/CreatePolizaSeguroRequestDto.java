package com.cloud_tecnoligical.nyxora_erp.dto.activosfijos;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreatePolizaSeguroRequestDto {

    @NotBlank(message = "El número de póliza es obligatorio")
    private String numero;

    private Long aseguradoraId;
    private String tipo;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private BigDecimal valorAsegurado;
}
