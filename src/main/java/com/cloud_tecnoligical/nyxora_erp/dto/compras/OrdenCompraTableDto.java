package com.cloud_tecnoligical.nyxora_erp.dto.compras;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrdenCompraTableDto {
    private Long id;
    private String numero;
    private Long proveedorId;
    private LocalDate fecha;
    private String estado;
    private BigDecimal total;
    private Boolean active;
}
