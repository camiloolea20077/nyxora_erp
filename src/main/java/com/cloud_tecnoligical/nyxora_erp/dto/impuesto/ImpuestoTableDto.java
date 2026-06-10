package com.cloud_tecnoligical.nyxora_erp.dto.impuesto;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ImpuestoTableDto {
    private Long id;
    private String codigo;
    private String nombre;
    private String tipo;
    private BigDecimal tarifa;
    private Boolean active;
}
