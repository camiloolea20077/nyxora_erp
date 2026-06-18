package com.cloud_tecnoligical.nyxora_erp.dto.juridico;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateFaltaRequestDto {

    private Long clasificacionFaltaId;

    @NotBlank(message = "El código es obligatorio")
    private String codigo;

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    private String descripcion;
    private Integer caducidadDias;
    private String politica;
}
