package com.cloud_tecnoligical.nyxora_erp.dto.juridico;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CambiarEstadoProcesoRequestDto {
    @NotBlank(message = "El estado es obligatorio")
    private String estado;
}
