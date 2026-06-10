package com.cloud_tecnoligical.nyxora_erp.dto.contabilidad;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ComprobanteTableDto {
    private Long id;
    private String numero;
    private LocalDate fecha;
    private String descripcion;
    private String estado;
    private BigDecimal totalDebito;
    private BigDecimal totalCredito;
    private Boolean active;
}
