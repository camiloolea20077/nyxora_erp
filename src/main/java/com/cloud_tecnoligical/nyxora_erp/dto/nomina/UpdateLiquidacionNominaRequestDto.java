package com.cloud_tecnoligical.nyxora_erp.dto.nomina;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateLiquidacionNominaRequestDto extends CreateLiquidacionNominaRequestDto {
    @NotNull(message = "El id es obligatorio")
    private Long id;
}
