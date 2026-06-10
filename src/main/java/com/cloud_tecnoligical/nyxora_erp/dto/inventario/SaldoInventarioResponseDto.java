package com.cloud_tecnoligical.nyxora_erp.dto.inventario;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SaldoInventarioResponseDto {
    private Long id;
    private Long bodegaId;
    private Long ubicacionId;
    private Long loteId;
    private Long productoId;
    private Long productoVarianteId;
    private BigDecimal cantidad;
    private BigDecimal costoPromedio;
    private BigDecimal valorTotal;
}
