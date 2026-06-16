package com.cloud_tecnoligical.nyxora_erp.dto.presupuesto;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PacPresupuestalResponseDto {
    private Long id;
    private Long rubroPresupuestalId;
    private Integer anio;
    private Integer mes;
    private BigDecimal valor;
}
