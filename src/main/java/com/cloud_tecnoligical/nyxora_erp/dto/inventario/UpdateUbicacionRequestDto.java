package com.cloud_tecnoligical.nyxora_erp.dto.inventario;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateUbicacionRequestDto extends CreateUbicacionRequestDto {

    @NotNull(message = "El id es obligatorio")
    private Long id;

    private Boolean active;
}
