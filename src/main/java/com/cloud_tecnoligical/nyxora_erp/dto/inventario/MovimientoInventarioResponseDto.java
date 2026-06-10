package com.cloud_tecnoligical.nyxora_erp.dto.inventario;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MovimientoInventarioResponseDto {
    private Long id;
    private Long bodegaId;
    private Long ubicacionId;
    private Long productoId;
    private Long productoVarianteId;
    private Long loteId;
    private String tipo;
    private LocalDate fecha;
    private BigDecimal cantidad;
    private BigDecimal costoUnitario;
    private BigDecimal descuentoPorcentaje;
    private BigDecimal descuentoValor;
    private Long impuestoId;
    private BigDecimal impuestoPorcentaje;
    private BigDecimal impuestoValor;
    private BigDecimal subtotal;
    private BigDecimal total;
    private Long centroCostoId;
    private Long terceroId;
    private String descripcion;
    private String origenModulo;
    private Long origenId;
    private LocalDateTime createdAt;
}
