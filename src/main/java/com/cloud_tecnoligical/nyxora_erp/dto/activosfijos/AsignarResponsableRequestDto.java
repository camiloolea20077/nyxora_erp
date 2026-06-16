package com.cloud_tecnoligical.nyxora_erp.dto.activosfijos;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AsignarResponsableRequestDto {

    @NotNull(message = "El tercero es obligatorio")
    private Long terceroId;
}
