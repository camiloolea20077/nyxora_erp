package com.cloud_tecnoligical.nyxora_erp.dto.cartera;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CuentaPorCobrarResponseDto {
    private Long id;
    private Long clienteId;
    private Long facturaId;
    private Long cuentaId;
    private LocalDate fechaEmision;
    private LocalDate fechaVencimiento;
    private Integer dias;
    private BigDecimal valorTotal;
    private BigDecimal valorInteres;
    private BigDecimal saldo;
    private LocalDate fechaUltimaLiquidacion;
    private String estado;
    private Boolean active;
    private LocalDateTime createdAt;
}
