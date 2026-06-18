package com.cloud_tecnoligical.nyxora_erp.dto.reportes;

import lombok.Getter;
import lombok.Setter;

/** Resultado del cierre orquestado de un periodo contable. */
@Getter
@Setter
public class CierrePeriodoResultDto {
    private Long periodoContableId;
    private Integer anio;
    private Integer mes;
    private String estado;
    private Long saldosRecalculados;
    private String mensaje;
}
