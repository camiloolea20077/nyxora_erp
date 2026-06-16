package com.cloud_tecnoligical.nyxora_erp.dto.contratacion;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CambiarEstadoContratoRequestDto {

    @NotBlank(message = "El estado es obligatorio")
    private String estado;
}
