package com.cloud_tecnoligical.nyxora_erp.dto.presupuesto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateRubroPresupuestalRequestDto {

    @NotNull(message = "La vigencia es obligatoria")
    private Long vigenciaId;

    private Long rubroPadreId;
    private String tipoRubro;          // ingreso | gasto

    @NotBlank(message = "El código del rubro es obligatorio")
    private String codigoRubro;

    @NotBlank(message = "El nombre del rubro es obligatorio")
    private String nombreRubro;

    private Boolean manejaMovimiento;
    private String homologacionCircularUnica;
}
