package com.cloud_tecnoligical.nyxora_erp.dto.presupuesto;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

/** Registra una afectación de la cadena de ejecución presupuestal. */
@Getter
@Setter
public class CreateAfectacionPresupuestalRequestDto {

    @NotNull(message = "El rubro presupuestal es obligatorio")
    private Long rubroPresupuestalId;

    @NotNull(message = "El tipo de operación es obligatorio")
    private String tipoOperacion;     // disponibilidad|compromiso|obligacion|pago|reconocimiento|recaudo

    private Long terceroId;
    private Long centroCostoId;
    private Long proyectoId;
    private Long fuenteFinanciamientoId;
    private Long cpcId;
    private String descripcion;

    @NotNull(message = "El valor es obligatorio")
    @Positive(message = "El valor debe ser positivo")
    private BigDecimal valor;
}
