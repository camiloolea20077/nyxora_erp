package com.cloud_tecnoligical.nyxora_erp.dto.vigencia;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateVigenciaRequestDto {

    @NotNull(message = "El id es obligatorio")
    private Long id;

    @NotNull(message = "El año es obligatorio")
    @Min(value = 2000, message = "El año debe ser >= 2000")
    @Max(value = 2100, message = "El año debe ser <= 2100")
    private Integer year;
}
