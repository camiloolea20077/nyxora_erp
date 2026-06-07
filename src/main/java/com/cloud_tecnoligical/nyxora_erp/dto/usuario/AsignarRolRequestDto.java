package com.cloud_tecnoligical.nyxora_erp.dto.usuario;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AsignarRolRequestDto {

    @NotNull(message = "El rolId es obligatorio")
    private Long rolId;

    @NotNull(message = "El sedeId es obligatorio")
    private Long sedeId;
}
