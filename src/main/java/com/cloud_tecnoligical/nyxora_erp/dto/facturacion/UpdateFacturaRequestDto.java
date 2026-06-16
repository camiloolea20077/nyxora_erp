package com.cloud_tecnoligical.nyxora_erp.dto.facturacion;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/** Actualiza una factura en 'borrador' (reemplaza las líneas). */
@Getter
@Setter
public class UpdateFacturaRequestDto extends CreateFacturaRequestDto {

    @NotNull(message = "El id es obligatorio")
    private Long id;
}
