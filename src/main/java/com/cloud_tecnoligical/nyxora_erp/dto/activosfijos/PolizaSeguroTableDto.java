package com.cloud_tecnoligical.nyxora_erp.dto.activosfijos;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PolizaSeguroTableDto {
    private Long id;
    private String numero;
    private String tipo;
    private BigDecimal valorAsegurado;
    private LocalDate fechaFin;
    private Boolean active;
}
