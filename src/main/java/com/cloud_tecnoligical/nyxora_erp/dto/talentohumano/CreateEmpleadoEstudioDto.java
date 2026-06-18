package com.cloud_tecnoligical.nyxora_erp.dto.talentohumano;

import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateEmpleadoEstudioDto {
    private Long nivelEstudioId;
    private String institucion;
    private String titulo;
    private LocalDate fechaInicial;
    private LocalDate fechaFinal;
    private LocalDate fechaGrado;
    private String numeroTarjetaProfesional;
    private Long municipioEstudioId;
    private Short semestresAprobados;
    private Boolean convalidado;
}
