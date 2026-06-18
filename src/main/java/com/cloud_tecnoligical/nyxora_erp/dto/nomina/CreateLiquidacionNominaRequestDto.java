package com.cloud_tecnoligical.nyxora_erp.dto.nomina;

import java.time.LocalDate;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateLiquidacionNominaRequestDto {

    private Long grupoNominaId;

    @NotNull(message = "El año es obligatorio")
    private Integer anio;

    @NotNull(message = "El mes es obligatorio")
    @Min(value = 1, message = "Mes inválido")
    @Max(value = 12, message = "Mes inválido")
    private Integer mes;

    private String periodo;

    @NotNull(message = "La fecha es obligatoria")
    private LocalDate fecha;
}
