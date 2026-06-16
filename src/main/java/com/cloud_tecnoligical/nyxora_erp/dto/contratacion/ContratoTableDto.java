package com.cloud_tecnoligical.nyxora_erp.dto.contratacion;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ContratoTableDto {
    private Long id;
    private String numero;
    private String nombre;
    private String contratistaNombre;
    private BigDecimal valor;
    private String estado;
    private LocalDate fechaInicio;
    private Boolean active;
}
