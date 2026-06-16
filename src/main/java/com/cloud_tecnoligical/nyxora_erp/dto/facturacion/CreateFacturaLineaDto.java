package com.cloud_tecnoligical.nyxora_erp.dto.facturacion;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateFacturaLineaDto {

    @NotNull(message = "El producto es obligatorio")
    private Long productoId;

    private Long productoVarianteId;
    private String descripcion;

    @NotNull(message = "La cantidad es obligatoria")
    @Positive(message = "La cantidad debe ser positiva")
    private BigDecimal cantidad;

    private Long unidadMedidaId;
    private BigDecimal valorUnitario;
    private BigDecimal descuentoPorcentaje;
    private BigDecimal descuentoValor;
    private Long impuestoId;
    private BigDecimal porcentajeImpuesto;
    private BigDecimal valorImpuesto;
    private Boolean discriminaIva;
    private Long bodegaId;
    private Long loteId;
    private Long centroCostoId;
}
