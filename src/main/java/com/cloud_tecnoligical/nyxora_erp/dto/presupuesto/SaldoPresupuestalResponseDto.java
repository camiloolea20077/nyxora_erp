package com.cloud_tecnoligical.nyxora_erp.dto.presupuesto;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SaldoPresupuestalResponseDto {
    private Long id;
    private Long rubroPresupuestalId;
    private Integer anio;
    private Integer mes;
    private BigDecimal planInicial;
    private BigDecimal adiciones;
    private BigDecimal reducciones;
    private BigDecimal aplazamientos;
    private BigDecimal creditos;
    private BigDecimal contraCreditos;
    private BigDecimal disponibilidad;
    private BigDecimal compromiso;
    private BigDecimal obligacion;
    private BigDecimal pagado;
    private BigDecimal reconocimientos;
    private BigDecimal recaudos;
    /** Apropiación neta = plan + adiciones − reducciones + créditos − contracréditos − aplazamientos. */
    private BigDecimal apropiacionNeta;
}
