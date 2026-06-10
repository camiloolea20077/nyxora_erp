package com.cloud_tecnoligical.nyxora_erp.dto.compras;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrdenCompraLineaResponseDto {
    private Long id;
    private Long ordenCompraId;
    private Long productoId;
    private Long productoVarianteId;
    private String descripcion;
    private BigDecimal cantidad;
    private Long unidadMedidaId;
    private BigDecimal valorUnitario;
    private BigDecimal descuentoPorcentaje;
    private BigDecimal descuentoValor;
    private Long impuestoId;
    private BigDecimal impuestoPorcentaje;
    private BigDecimal impuestoValor;
    private BigDecimal subtotal;
    private BigDecimal total;
    private BigDecimal cantidadRecibida;
    private BigDecimal cantidadPendiente;
    private Long centroCostoId;
}
