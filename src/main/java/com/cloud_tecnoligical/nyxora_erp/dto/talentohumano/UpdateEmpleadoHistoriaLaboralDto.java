package com.cloud_tecnoligical.nyxora_erp.dto.talentohumano;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateEmpleadoHistoriaLaboralDto extends CreateEmpleadoHistoriaLaboralDto {
    @NotNull(message = "El id es obligatorio")
    private Long id;
}
