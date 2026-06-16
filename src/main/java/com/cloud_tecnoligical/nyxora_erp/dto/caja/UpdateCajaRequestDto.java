package com.cloud_tecnoligical.nyxora_erp.dto.caja;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateCajaRequestDto extends CreateCajaRequestDto {

    @NotNull(message = "El id es obligatorio")
    private Long id;
}
