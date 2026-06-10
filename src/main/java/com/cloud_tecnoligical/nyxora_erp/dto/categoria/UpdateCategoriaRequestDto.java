package com.cloud_tecnoligical.nyxora_erp.dto.categoria;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/** Hereda los campos editables de Create y agrega id + active. */
@Getter
@Setter
public class UpdateCategoriaRequestDto extends CreateCategoriaRequestDto {

    @NotNull(message = "El id es obligatorio")
    private Long id;

    private Boolean active;
}
