package com.cloud_tecnoligical.nyxora_erp.dto.inventario;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

/**
 * Registro de un movimiento de inventario. cantidad = magnitud (>0) para entrada/salida;
 * para 'ajuste' puede ser negativa. El traslado usa otro endpoint.
 */
@Getter
@Setter
public class CreateMovimientoInventarioDto {

    @NotNull(message = "La bodega es obligatoria")
    private Long bodegaId;

    private Long ubicacionId;

    @NotNull(message = "El producto es obligatorio")
    private Long productoId;

    private Long productoVarianteId;
    private Long loteId;

    @NotBlank(message = "El tipo es obligatorio")
    @Pattern(regexp = "entrada|salida|ajuste", message = "El tipo debe ser 'entrada', 'salida' o 'ajuste'")
    private String tipo;

    @NotNull(message = "La fecha es obligatoria")
    private LocalDate fecha;

    @NotNull(message = "La cantidad es obligatoria")
    private BigDecimal cantidad;

    private BigDecimal costoUnitario;
    private BigDecimal descuentoPorcentaje;
    private BigDecimal descuentoValor;
    private Long impuestoId;
    private BigDecimal impuestoPorcentaje;
    private BigDecimal impuestoValor;
    private Long centroCostoId;
    private Long terceroId;
    private String descripcion;
    private String origenModulo;
    private Long origenId;
}
