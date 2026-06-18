package com.cloud_tecnoligical.nyxora_erp.dto.nomina;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CargoResponseDto {
    private Long id;
    private String codigo;
    private String nombre;
    private String nivelCargo;
    private String grado;
    private String tipoRemuneracion;
    private BigDecimal sueldoBasico;
    private BigDecimal sueldoMaximo;
    private String mision;
    private String descripcion;
    private Boolean active;
    private LocalDateTime createdAt;
}
