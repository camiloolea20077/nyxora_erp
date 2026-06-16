package com.cloud_tecnoligical.nyxora_erp.dto.activosfijos;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateActivoFijoRequestDto extends CreateActivoFijoRequestDto {

    @NotNull(message = "El id es obligatorio")
    private Long id;
}
