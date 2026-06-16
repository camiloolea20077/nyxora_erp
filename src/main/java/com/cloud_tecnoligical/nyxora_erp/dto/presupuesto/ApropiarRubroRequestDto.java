package com.cloud_tecnoligical.nyxora_erp.dto.presupuesto;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/** Define/ajusta la apropiación presupuestal de un rubro para un año (mes 0 = anual). */
@Getter
@Setter
public class ApropiarRubroRequestDto {

    @NotNull(message = "El rubro presupuestal es obligatorio")
    private Long rubroPresupuestalId;

    @NotNull(message = "El año es obligatorio")
    private Integer anio;

    private BigDecimal planInicial;
    private BigDecimal adiciones;
    private BigDecimal reducciones;
    private BigDecimal aplazamientos;
    private BigDecimal creditos;
    private BigDecimal contraCreditos;
}
