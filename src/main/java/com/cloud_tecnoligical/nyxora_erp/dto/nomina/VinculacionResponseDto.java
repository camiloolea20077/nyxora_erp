package com.cloud_tecnoligical.nyxora_erp.dto.nomina;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VinculacionResponseDto {
    private Long id;
    private Long empleadoId;
    private String empleadoNombre;
    private Long cargoId;
    private String cargoNombre;
    private Long grupoNominaId;
    private String codigo;
    private LocalDate fecha;
    private LocalDate fechaFin;
    private String tipoVinculacion;
    private String tipoContrato;
    private BigDecimal sueldo;
    private Integer horaTrabajo;
    private Boolean periodoPrueba;
    private LocalDate fechaFinPeriodoPrueba;
    private String frecuenciaPago;
    private Long jefeId;
    private Long sedeId;
    private Long dependenciaId;
    private Long municipioVinculacionId;
    private String tipoCotizante;
    private String estadoVinculacion;
    private String objeto;
    private Boolean temporal;
    private Boolean active;
    private LocalDateTime createdAt;
}
