package com.cloud_tecnoligical.nyxora_erp.dto.tesoreria;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateCuentaBancariaRequestDto extends CreateCuentaBancariaRequestDto {

    @NotNull(message = "El id es obligatorio")
    private Long id;
}
