package com.cloud_tecnoligical.nyxora_erp.dto.reportes;

import java.math.BigDecimal;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

/** Balance general básico (activo = pasivo + patrimonio) de un periodo. */
@Getter
@Setter
public class BalanceGeneralDto {
    private Long periodoContableId;
    private List<BalanceLineaDto> activos;
    private List<BalanceLineaDto> pasivos;
    private List<BalanceLineaDto> patrimonio;
    private BigDecimal totalActivo;
    private BigDecimal totalPasivo;
    private BigDecimal totalPatrimonio;
    private BigDecimal totalPasivoPatrimonio;
    /** total activo - (pasivo + patrimonio); idealmente 0 si está cuadrado. */
    private BigDecimal descuadre;
}
