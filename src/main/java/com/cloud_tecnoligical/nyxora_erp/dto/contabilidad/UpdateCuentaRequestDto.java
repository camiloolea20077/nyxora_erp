package com.cloud_tecnoligical.nyxora_erp.dto.contabilidad;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateCuentaRequestDto extends CreateCuentaRequestDto {

    @NotNull(message = "El id es obligatorio")
    private Long id;

    private Boolean active;
}
