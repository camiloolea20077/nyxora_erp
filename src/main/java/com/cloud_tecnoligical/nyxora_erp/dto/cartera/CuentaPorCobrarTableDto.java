package com.cloud_tecnoligical.nyxora_erp.dto.cartera;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CuentaPorCobrarTableDto {
    private Long id;
    private Long clienteId;
    private Long facturaId;
    private LocalDate fechaEmision;
    private LocalDate fechaVencimiento;
    private BigDecimal valorTotal;
    private BigDecimal saldo;
    private String estado;
    private Boolean active;
}
