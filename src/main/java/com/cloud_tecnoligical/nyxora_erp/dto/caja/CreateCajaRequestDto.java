package com.cloud_tecnoligical.nyxora_erp.dto.caja;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateCajaRequestDto {

    @NotBlank(message = "El código es obligatorio")
    private String codigo;

    private String nombre;
    private Long sedeId;
    private Long usuarioId;
}
