package com.cloud_tecnoligical.nyxora_erp.dto.presupuesto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FuenteFinanciamientoTableDto {
    private Long id;
    private String codigo;
    private String nombre;
    private String tipoRecurso;
    private Boolean active;
}
