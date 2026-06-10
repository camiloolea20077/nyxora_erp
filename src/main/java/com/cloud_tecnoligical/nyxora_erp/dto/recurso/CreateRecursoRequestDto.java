package com.cloud_tecnoligical.nyxora_erp.dto.recurso;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateRecursoRequestDto {

    @NotBlank(message = "El código es obligatorio")
    @Size(max = 10)
    private String codigo;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100)
    private String nombre;

    private String tipoRecurso;
    private String driver;
    private Boolean costoAdicional;
    private String descripcion;
}
