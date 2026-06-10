package com.cloud_tecnoligical.nyxora_erp.dto.compras;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateRecepcionLineaDto {

    @NotNull(message = "La línea de la orden es obligatoria")
    private Long ordenCompraLineaId;

    @NotNull(message = "El producto es obligatorio")
    private Long productoId;

    private Long productoVarianteId;
    private Long loteId;
    private Long ubicacionId;

    @NotNull(message = "La cantidad recibida es obligatoria")
    @Positive(message = "La cantidad recibida debe ser positiva")
    private BigDecimal cantidadRecibida;

    private BigDecimal costoUnitario;
}
