package com.cloud_tecnoligical.nyxora_erp.dto.reportes;

import java.math.BigDecimal;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

/** Estado de resultados básico (ingresos - costos/gastos = utilidad) de un periodo. */
@Getter
@Setter
public class EstadoResultadosDto {
    private Long periodoContableId;
    private List<BalanceLineaDto> ingresos;
    private List<BalanceLineaDto> costosGastos;
    private BigDecimal totalIngresos;
    private BigDecimal totalCostosGastos;
    private BigDecimal utilidad;
}
