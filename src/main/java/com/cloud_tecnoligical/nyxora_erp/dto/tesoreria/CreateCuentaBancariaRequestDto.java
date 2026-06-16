package com.cloud_tecnoligical.nyxora_erp.dto.tesoreria;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateCuentaBancariaRequestDto {

    @NotNull(message = "El banco es obligatorio")
    private Long bancoId;

    private Long tipoCuentaBancariaId;

    @NotBlank(message = "El número de cuenta es obligatorio")
    private String numeroCuenta;

    private Long cuentaContableId;
    private Boolean manejaSobregiro;
    private Boolean aceptaTransferencias;
    private LocalDate fechaExpiracion;
}
