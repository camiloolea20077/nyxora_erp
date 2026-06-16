package com.cloud_tecnoligical.nyxora_erp.dto.presupuesto;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RubroPresupuestalResponseDto {
    private Long id;
    private Long vigenciaId;
    private Long rubroPadreId;
    private String tipoRubro;
    private String codigoRubro;
    private String nombreRubro;
    private Boolean manejaMovimiento;
    private String homologacionCircularUnica;
    private Integer nivel;
    private Boolean active;
    private LocalDateTime createdAt;
}
