package com.cloud_tecnoligical.nyxora_erp.dto.tercero;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TerceroDireccionResponseDto {
    private Long id;
    private Long terceroId;
    private String tipo;
    private String direccion;
    private Long municipioId;
    private Long barrioId;
    private String codigoPostal;
    private String telefono;
    private Boolean principal;
    private Boolean active;
}
