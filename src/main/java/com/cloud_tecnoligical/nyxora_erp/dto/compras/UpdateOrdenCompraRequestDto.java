package com.cloud_tecnoligical.nyxora_erp.dto.compras;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/** Actualiza una orden en 'borrador' (reemplaza las líneas). */
@Getter
@Setter
public class UpdateOrdenCompraRequestDto extends CreateOrdenCompraRequestDto {

    @NotNull(message = "El id es obligatorio")
    private Long id;
}
