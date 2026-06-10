package com.cloud_tecnoligical.nyxora_erp.dto.categoria;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateCategoriaRequestDto {

    private Long categoriaPadreId;

    @NotBlank(message = "El código es obligatorio")
    @Size(max = 20)
    private String codigo;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 150)
    private String nombre;

    private String tipoProducto;
    private String metodoCosteo;
    private Integer izquierda;
    private Integer derecha;
    private Integer nivel;
}
