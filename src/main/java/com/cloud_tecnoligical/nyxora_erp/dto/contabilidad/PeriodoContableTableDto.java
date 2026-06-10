package com.cloud_tecnoligical.nyxora_erp.dto.contabilidad;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PeriodoContableTableDto {
    private Long id;
    private Long vigenciaId;
    private Integer anio;
    private Integer mes;
    private String estado;
}
