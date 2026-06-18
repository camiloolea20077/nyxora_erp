package com.cloud_tecnoligical.nyxora_erp.dto.nomina;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateConceptoNominaRequestDto {

    @NotBlank(message = "El código es obligatorio")
    private String codigo;

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    private String frecuencia;

    /** devengado | deduccion | provision | aporte */
    @NotBlank(message = "La clase es obligatoria")
    private String clase;

    private String formula;
    private Long cuentaCreditoId;
    private Long cuentaPatronoId;
    private Long rubroPresupuestalId;
    private Long fuenteFinanciamientoId;
    private Long terceroId;
}
