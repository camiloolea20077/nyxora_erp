package com.cloud_tecnoligical.nyxora_erp.dto.contratacion;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AsignarPolizaContratoRequestDto {

    @NotNull(message = "La póliza es obligatoria")
    private Long polizaSeguroId;
}
