package com.cloud_tecnoligical.nyxora_erp.dto.caja;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReciboCajaTableDto {
    private Long id;
    private Long cajaId;
    private String numero;
    private Long clienteId;
    private LocalDate fecha;
    private BigDecimal valor;
    private String estado;
    private Boolean active;
}
