package com.cloud_tecnoligical.nyxora_erp.dto.nomina;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CargoTableDto {
    private Long id;
    private String codigo;
    private String nombre;
    private String nivelCargo;
    private BigDecimal sueldoBasico;
    private Boolean active;
}
