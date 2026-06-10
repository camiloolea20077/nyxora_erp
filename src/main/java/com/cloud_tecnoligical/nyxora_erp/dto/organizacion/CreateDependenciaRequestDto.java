package com.cloud_tecnoligical.nyxora_erp.dto.organizacion;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateDependenciaRequestDto {

    private Long centroCostoId;
    private Long dependenciaPadreId;

    @NotBlank(message = "El código es obligatorio")
    @Size(max = 40)
    private String codigo;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 191)
    private String nombre;

    private String ubicacion;
    private String latitud;
    private String longitud;
    private Integer izquierda;
    private Integer derecha;
    private Integer nivel;
}
