package com.cloud_tecnoligical.nyxora_erp.dto.usuario;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UsuarioTableDto {
    private Long id;
    private String username;
    private String email;
    private String terceroNombre;
    private Boolean active;
}
