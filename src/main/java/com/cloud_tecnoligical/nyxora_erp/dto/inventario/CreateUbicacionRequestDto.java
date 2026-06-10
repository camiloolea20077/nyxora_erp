package com.cloud_tecnoligical.nyxora_erp.dto.inventario;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateUbicacionRequestDto {

    @NotNull(message = "La bodega es obligatoria")
    private Long bodegaId;

    private Long ubicacionPadreId;

    @NotBlank(message = "El código es obligatorio")
    @Size(max = 20)
    private String codigo;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 150)
    private String nombre;

    private Integer pasillo;
    private Integer altura;
    private Integer posicion;
    private Integer izquierda;
    private Integer derecha;
    private Integer nivel;
}
