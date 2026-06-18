package com.cloud_tecnoligical.nyxora_erp.dto.juridico;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddProcesoFaltaDto {
    @NotNull(message = "La falta es obligatoria")
    private Long faltaId;
}
