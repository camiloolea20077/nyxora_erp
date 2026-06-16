package com.cloud_tecnoligical.nyxora_erp.dto.facturacion;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FacturaTableDto {
    private Long id;
    private String numero;
    private Long clienteId;
    private LocalDate fecha;
    private String estado;
    private BigDecimal total;
    private Boolean active;
}
