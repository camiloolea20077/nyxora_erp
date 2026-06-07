package com.cloud_tecnoligical.nyxora_erp.dto.auth;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TokenResponseDto {

    private String accessToken;
    private String refreshToken;
    private String tokenType;
    private long expiresIn;       // segundos
    private Long usuarioId;
    private String username;
    private Long empresaId;
    private List<String> permisos;

    public TokenResponseDto(String accessToken, String refreshToken, long expiresIn,
                            Long usuarioId, String username, Long empresaId, List<String> permisos) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.tokenType = "Bearer";
        this.expiresIn = expiresIn;
        this.usuarioId = usuarioId;
        this.username = username;
        this.empresaId = empresaId;
        this.permisos = permisos;
    }
}
