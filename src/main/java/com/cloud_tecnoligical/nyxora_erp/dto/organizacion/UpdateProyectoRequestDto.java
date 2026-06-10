package com.cloud_tecnoligical.nyxora_erp.dto.organizacion;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateProyectoRequestDto extends CreateProyectoRequestDto {

    @NotNull(message = "El id es obligatorio")
    private Long id;

    private Boolean active;
}
