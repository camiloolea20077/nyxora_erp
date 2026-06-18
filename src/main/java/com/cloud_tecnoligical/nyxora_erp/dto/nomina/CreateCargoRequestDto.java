package com.cloud_tecnoligical.nyxora_erp.dto.nomina;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateCargoRequestDto {

    @NotBlank(message = "El código es obligatorio")
    private String codigo;

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    private String nivelCargo;
    private String grado;
    private String tipoRemuneracion;
    private BigDecimal sueldoBasico;
    private BigDecimal sueldoMaximo;
    private String mision;
    private String descripcion;
}
