package com.cloud_tecnoligical.nyxora_erp.dto.usuario;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

/** Nunca expone hash_password. */
@Getter
@Setter
public class UsuarioResponseDto {
    private Long id;
    private Long terceroId;
    private String terceroNombre;
    private String username;
    private String email;
    private Boolean active;
    private LocalDateTime createdAt;
}
