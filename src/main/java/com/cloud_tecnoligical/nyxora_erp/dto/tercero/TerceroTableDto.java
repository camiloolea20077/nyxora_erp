package com.cloud_tecnoligical.nyxora_erp.dto.tercero;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TerceroTableDto {
    private Long id;
    private String tipoDocumento;
    private String numeroDocumento;
    private String nombre;
    private String tipoPersona;
    private Boolean active;
}
