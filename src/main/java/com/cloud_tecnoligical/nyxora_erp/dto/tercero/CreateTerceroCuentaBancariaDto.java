package com.cloud_tecnoligical.nyxora_erp.dto.tercero;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateTerceroCuentaBancariaDto {
    @NotNull(message = "El banco es obligatorio")
    private Long bancoId;
    @NotNull(message = "El tipo de cuenta es obligatorio")
    private Long tipoCuentaBancariaId;
    @NotBlank(message = "El número de cuenta es obligatorio")
    private String numeroCuenta;
    private Boolean principal;
}
