package com.cloud_tecnoligical.nyxora_erp.dto.activosfijos;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AsignarPolizaRequestDto {

    @NotNull(message = "La póliza es obligatoria")
    private Long polizaSeguroId;
}
