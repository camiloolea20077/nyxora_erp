package com.cloud_tecnoligical.nyxora_erp.dto.organizacion;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateDependenciaRequestDto extends CreateDependenciaRequestDto {

    @NotNull(message = "El id es obligatorio")
    private Long id;

    private Boolean active;
}
