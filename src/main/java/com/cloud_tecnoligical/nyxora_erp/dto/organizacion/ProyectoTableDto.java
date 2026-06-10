package com.cloud_tecnoligical.nyxora_erp.dto.organizacion;

import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProyectoTableDto {
    private Long id;
    private String codigo;
    private String nombre;
    private LocalDate fechaInicio;
    private LocalDate fechaFinal;
    private Boolean active;
}
