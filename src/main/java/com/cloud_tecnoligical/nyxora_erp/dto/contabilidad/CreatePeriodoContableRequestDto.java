package com.cloud_tecnoligical.nyxora_erp.dto.contabilidad;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreatePeriodoContableRequestDto {

    @NotNull(message = "La vigencia es obligatoria")
    private Long vigenciaId;

    @NotNull(message = "El año es obligatorio")
    private Integer anio;

    @NotNull(message = "El mes es obligatorio")
    @Min(value = 1, message = "El mes debe estar entre 1 y 12")
    @Max(value = 12, message = "El mes debe estar entre 1 y 12")
    private Integer mes;
}
