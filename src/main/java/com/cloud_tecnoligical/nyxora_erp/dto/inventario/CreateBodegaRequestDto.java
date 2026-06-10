package com.cloud_tecnoligical.nyxora_erp.dto.inventario;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateBodegaRequestDto {

    private Long sedeId;
    private Long centroCostoId;

    @NotBlank(message = "El código es obligatorio")
    @Size(max = 20)
    private String codigo;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 150)
    private String nombre;

    private String tipoAbastecimiento;
    private String direccion;
    private String latitud;
    private String longitud;
    private Boolean permiteCompra;
}
