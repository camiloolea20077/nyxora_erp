package com.cloud_tecnoligical.nyxora_erp.dto.facturacion;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FacturaLineaResponseDto {
    private Long id;
    private Long facturaId;
    private Long productoId;
    private Long productoVarianteId;
    private String descripcion;
    private BigDecimal cantidad;
    private Long unidadMedidaId;
    private BigDecimal valorUnitario;
    private BigDecimal descuentoPorcentaje;
    private BigDecimal descuentoValor;
    private BigDecimal subtotal;
    private Long impuestoId;
    private BigDecimal porcentajeImpuesto;
    private BigDecimal valorImpuesto;
    private Boolean discriminaIva;
    private BigDecimal total;
    private Long bodegaId;
    private Long loteId;
    private Long centroCostoId;
}
