package com.cloud_tecnoligical.nyxora_erp.dto.organizacion;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateCentroCostoRequestDto {

    private Long sedeId;
    private Long centroCostoPadreId;

    @NotBlank(message = "El código es obligatorio")
    @Size(max = 20)
    private String codigo;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 150)
    private String nombre;

    private String tipoCentroCosto;
    private String claseCentroCosto;
    private Boolean esObservacion;
    private Boolean manejaPlanFinanciero;
    private Long terceroId;
    private String direccion;
    private Long unidadNegocioId;
    private Integer izquierda;
    private Integer derecha;
    private Integer nivel;
}
