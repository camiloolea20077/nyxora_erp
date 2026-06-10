package com.cloud_tecnoligical.nyxora_erp.dto.documento;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateTipoDocumentoRequestDto extends CreateTipoDocumentoRequestDto {

    @NotNull(message = "El id es obligatorio")
    private Long id;

    private Boolean active;
}
