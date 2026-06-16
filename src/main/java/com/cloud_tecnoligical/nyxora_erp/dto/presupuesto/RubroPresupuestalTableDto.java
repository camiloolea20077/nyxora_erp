package com.cloud_tecnoligical.nyxora_erp.dto.presupuesto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RubroPresupuestalTableDto {
    private Long id;
    private Long vigenciaId;
    private Long rubroPadreId;
    private String tipoRubro;
    private String codigoRubro;
    private String nombreRubro;
    private Boolean manejaMovimiento;
    private Boolean active;
}
