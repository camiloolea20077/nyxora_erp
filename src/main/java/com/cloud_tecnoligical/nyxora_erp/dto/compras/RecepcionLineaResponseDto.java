package com.cloud_tecnoligical.nyxora_erp.dto.compras;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RecepcionLineaResponseDto {
    private Long id;
    private Long recepcionId;
    private Long ordenCompraLineaId;
    private Long productoId;
    private Long productoVarianteId;
    private Long loteId;
    private Long ubicacionId;
    private BigDecimal cantidadRecibida;
    private BigDecimal costoUnitario;
}
