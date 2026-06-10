package com.cloud_tecnoligical.nyxora_erp.dto.inventario;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

/** Traslado entre bodegas: genera 2 movimientos (origen − / destino +). */
@Getter
@Setter
public class TrasladoInventarioDto {

    @NotNull(message = "La bodega origen es obligatoria")
    private Long bodegaOrigenId;

    @NotNull(message = "La bodega destino es obligatoria")
    private Long bodegaDestinoId;

    private Long ubicacionOrigenId;
    private Long ubicacionDestinoId;

    @NotNull(message = "El producto es obligatorio")
    private Long productoId;

    private Long productoVarianteId;
    private Long loteId;

    @NotNull(message = "La fecha es obligatoria")
    private LocalDate fecha;

    @NotNull(message = "La cantidad es obligatoria")
    @Positive(message = "La cantidad del traslado debe ser positiva")
    private BigDecimal cantidad;

    private BigDecimal costoUnitario;
    private String descripcion;
}
