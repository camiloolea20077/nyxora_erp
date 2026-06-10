package com.cloud_tecnoligical.nyxora_erp.dto.documento;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TipoDocumentoTableDto {
    private Long id;
    private String modulo;
    private String codigo;
    private String nombre;
    private String prefijo;
    private Boolean active;
}
