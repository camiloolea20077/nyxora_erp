package com.cloud_tecnoligical.nyxora_erp.dto.nomina;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ConceptoNominaResponseDto {
    private Long id;
    private String codigo;
    private String nombre;
    private String frecuencia;
    private String clase;
    private String formula;
    private Long cuentaCreditoId;
    private Long cuentaPatronoId;
    private Long rubroPresupuestalId;
    private Long fuenteFinanciamientoId;
    private Long terceroId;
    private Boolean active;
    private LocalDateTime createdAt;
}
