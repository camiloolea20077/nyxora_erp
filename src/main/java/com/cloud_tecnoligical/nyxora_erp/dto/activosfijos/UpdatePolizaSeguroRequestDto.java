package com.cloud_tecnoligical.nyxora_erp.dto.activosfijos;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdatePolizaSeguroRequestDto extends CreatePolizaSeguroRequestDto {

    @NotNull(message = "El id es obligatorio")
    private Long id;
}
