package com.cloud_tecnoligical.nyxora_erp.dto.contabilidad;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/** Línea de un comprobante. débito XOR crédito (uno debe ir en 0). */
@Getter
@Setter
public class CreateMovimientoContableDto {

    @NotNull(message = "La cuenta es obligatoria")
    private Long cuentaId;

    private Long terceroId;
    private Long centroCostoId;
    private Long proyectoId;
    private Long recursoId;
    private String descripcion;

    private BigDecimal debito;
    private BigDecimal credito;
    private BigDecimal valorBase;
    private Long impuestoId;
    private BigDecimal porcentajeImpuesto;
    private BigDecimal valorTrm;
    private BigDecimal valorDolar;
}
