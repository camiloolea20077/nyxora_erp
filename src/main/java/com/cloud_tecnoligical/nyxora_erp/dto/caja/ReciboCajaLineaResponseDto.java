package com.cloud_tecnoligical.nyxora_erp.dto.caja;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReciboCajaLineaResponseDto {
    private Long id;
    private Long reciboCajaId;
    private Long cuentaPorCobrarId;
    private BigDecimal valorAplicado;
}
