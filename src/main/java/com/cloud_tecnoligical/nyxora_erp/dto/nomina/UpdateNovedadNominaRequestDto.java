package com.cloud_tecnoligical.nyxora_erp.dto.nomina;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateNovedadNominaRequestDto extends CreateNovedadNominaRequestDto {
    @NotNull(message = "El id es obligatorio")
    private Long id;
    private Boolean active;
}
