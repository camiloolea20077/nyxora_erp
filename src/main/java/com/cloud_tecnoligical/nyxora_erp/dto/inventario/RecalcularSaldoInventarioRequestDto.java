package com.cloud_tecnoligical.nyxora_erp.dto.inventario;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RecalcularSaldoInventarioRequestDto {

    @NotNull(message = "La bodega es obligatoria")
    private Long bodegaId;
}
