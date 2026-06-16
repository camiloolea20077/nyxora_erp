package com.cloud_tecnoligical.nyxora_erp.dto.cuentaspagar;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ObligacionPagoTableDto {
    private Long id;
    private Long proveedorId;
    private Long facturaProveedorId;
    private String numero;
    private LocalDate fecha;
    private LocalDate fechaVencimiento;
    private BigDecimal valorTotal;
    private BigDecimal saldo;
    private String estado;
    private Boolean active;
}
