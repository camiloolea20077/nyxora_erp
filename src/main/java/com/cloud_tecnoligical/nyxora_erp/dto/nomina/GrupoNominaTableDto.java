package com.cloud_tecnoligical.nyxora_erp.dto.nomina;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GrupoNominaTableDto {
    private Long id;
    private String codigo;
    private String nombre;
    private String frecuenciaPago;
    private Boolean active;
}
