package com.cloud_tecnoligical.nyxora_erp.dto.caja;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CajaTableDto {
    private Long id;
    private String codigo;
    private String nombre;
    private String estado;
    private BigDecimal saldoInicial;
    private Boolean active;
}
