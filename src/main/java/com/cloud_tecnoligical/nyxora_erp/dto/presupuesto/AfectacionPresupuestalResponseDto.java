package com.cloud_tecnoligical.nyxora_erp.dto.presupuesto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AfectacionPresupuestalResponseDto {
    private Long id;
    private Long rubroPresupuestalId;
    private String tipoOperacion;
    private Long terceroId;
    private Long centroCostoId;
    private Long proyectoId;
    private Long fuenteFinanciamientoId;
    private Long cpcId;
    private String descripcion;
    private BigDecimal valor;
    private BigDecimal subtotal;
    private BigDecimal saldo;
    private String origenModulo;
    private Long origenId;
    private LocalDateTime createdAt;
}
