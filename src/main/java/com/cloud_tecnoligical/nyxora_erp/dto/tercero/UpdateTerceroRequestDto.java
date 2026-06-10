package com.cloud_tecnoligical.nyxora_erp.dto.tercero;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/** Hereda todos los campos editables de Create y agrega id + active. */
@Getter
@Setter
public class UpdateTerceroRequestDto extends CreateTerceroRequestDto {

    @NotNull(message = "El id es obligatorio")
    private Long id;

    private Boolean active;
}
