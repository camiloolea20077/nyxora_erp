package com.cloud_tecnoligical.nyxora_erp.dto.contratacion;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateClausulaPlantillaRequestDto extends CreateClausulaPlantillaRequestDto {

    @NotNull(message = "El id es obligatorio")
    private Long id;
}
