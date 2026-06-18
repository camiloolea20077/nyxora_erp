package com.cloud_tecnoligical.nyxora_erp.dto.tesoreria;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CuentaBancariaResponseDto {
    private Long id;
    private Long bancoId;
    private String bancoNombre;
    private Long tipoCuentaBancariaId;
    private String numeroCuenta;
    private Long cuentaContableId;
    private Boolean manejaSobregiro;
    private Boolean aceptaTransferencias;
    private LocalDate fechaExpiracion;
    private Boolean active;
    private LocalDateTime createdAt;
}
