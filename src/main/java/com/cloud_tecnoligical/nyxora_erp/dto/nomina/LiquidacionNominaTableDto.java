package com.cloud_tecnoligical.nyxora_erp.dto.nomina;

import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LiquidacionNominaTableDto {
    private Long id;
    private Integer anio;
    private Integer mes;
    private String periodo;
    private LocalDate fecha;
    private String estado;
    private Boolean active;
}
