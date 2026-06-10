package com.cloud_tecnoligical.nyxora_erp.dto.organizacion;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateProyectoRequestDto {

    @NotBlank(message = "El código es obligatorio")
    @Size(max = 50)
    private String codigo;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 150)
    private String nombre;

    private String descripcion;
    private Long programaId;
    private LocalDate fechaInicio;
    private LocalDate fechaFinal;
}
