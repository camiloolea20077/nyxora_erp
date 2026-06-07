package com.cloud_tecnoligical.nyxora_erp.dto.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RefreshRequestDto {

    @NotBlank(message = "El refreshToken es obligatorio")
    private String refreshToken;
}
