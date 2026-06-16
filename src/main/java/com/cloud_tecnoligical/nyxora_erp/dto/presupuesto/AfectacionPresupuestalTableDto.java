package com.cloud_tecnoligical.nyxora_erp.dto.presupuesto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AfectacionPresupuestalTableDto {
    private Long id;
    private Long rubroPresupuestalId;
    private String tipoOperacion;
    private BigDecimal valor;
    private LocalDateTime createdAt;
}
