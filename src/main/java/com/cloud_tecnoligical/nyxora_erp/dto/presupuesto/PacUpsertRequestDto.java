package com.cloud_tecnoligical.nyxora_erp.dto.presupuesto;

import java.math.BigDecimal;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/** Define el valor del PAC para un rubro en un mes. */
@Getter
@Setter
public class PacUpsertRequestDto {

    @NotNull(message = "El rubro presupuestal es obligatorio")
    private Long rubroPresupuestalId;

    @NotNull(message = "El año es obligatorio")
    private Integer anio;

    @NotNull(message = "El mes es obligatorio")
    @Min(value = 1, message = "El mes debe estar entre 1 y 12")
    @Max(value = 12, message = "El mes debe estar entre 1 y 12")
    private Integer mes;

    @NotNull(message = "El valor es obligatorio")
    private BigDecimal valor;
}
