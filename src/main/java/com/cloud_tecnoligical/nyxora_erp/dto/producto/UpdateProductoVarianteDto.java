package com.cloud_tecnoligical.nyxora_erp.dto.producto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateProductoVarianteDto extends CreateProductoVarianteDto {

    @NotNull(message = "El id es obligatorio")
    private Long id;

    private Boolean active;
}
