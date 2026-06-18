package com.cloud_tecnoligical.nyxora_erp.dto.nomina;

import lombok.Getter;
import lombok.Setter;

/**
 * Parámetros de la liquidación. {@code conceptoSueldoId} (opcional): concepto devengado usado para
 * generar la línea de sueldo básico por vinculación; si es null, el detalle se arma solo desde las novedades.
 */
@Getter
@Setter
public class LiquidarNominaRequestDto {
    private Long conceptoSueldoId;
}
