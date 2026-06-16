package com.cloud_tecnoligical.nyxora_erp.dto.caja;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ArqueoTableDto {
    private Long id;
    private Long cajaId;
    private LocalDateTime fecha;
    private BigDecimal valorDeclarado;
    private BigDecimal valorSistema;
    private BigDecimal diferencia;
}
