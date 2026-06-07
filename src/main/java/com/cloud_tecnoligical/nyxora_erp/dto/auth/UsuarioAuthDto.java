package com.cloud_tecnoligical.nyxora_erp.dto.auth;

import lombok.Getter;
import lombok.Setter;

/**
 * Proyección mínima para autenticación (se llena desde SQL nativo con alias "camelCase").
 */
@Getter
@Setter
public class UsuarioAuthDto {
    private Long id;
    private Long empresaId;
    private String hashPassword;
    private Boolean activo;
}
