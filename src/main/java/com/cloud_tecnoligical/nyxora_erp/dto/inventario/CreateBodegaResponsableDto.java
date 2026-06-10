package com.cloud_tecnoligical.nyxora_erp.dto.inventario;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateBodegaResponsableDto {

    @NotNull(message = "El tercero es obligatorio")
    private Long terceroId;

    private Boolean predeterminado;
}
