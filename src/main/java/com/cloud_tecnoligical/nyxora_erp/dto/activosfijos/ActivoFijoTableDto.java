package com.cloud_tecnoligical.nyxora_erp.dto.activosfijos;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ActivoFijoTableDto {
    private Long id;
    private String codigo;
    private String nombre;
    private String numeroSerie;
    private BigDecimal valorCompra;
    private BigDecimal valorActual;
    private String estadoActivo;
    private Boolean active;
}
