package com.cloud_tecnoligical.nyxora_erp.dto.tercero;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TerceroContactoResponseDto {
    private Long id;
    private Long terceroId;
    private String nombre;
    private String cargo;
    private String telefono;
    private String celular;
    private String email;
    private String notas;
    private Boolean principal;
    private Boolean active;
}
