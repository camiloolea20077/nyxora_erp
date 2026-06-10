package com.cloud_tecnoligical.nyxora_erp.dto.documento;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateTipoDocumentoRequestDto {

    @NotBlank(message = "El módulo es obligatorio")
    @Size(max = 30)
    private String modulo;

    @NotBlank(message = "El código es obligatorio")
    @Size(max = 20)
    private String codigo;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 255)
    private String nombre;

    @Size(max = 10)
    private String prefijo;

    private Boolean reiniciaPorVigencia;
}
