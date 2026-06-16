package com.cloud_tecnoligical.nyxora_erp.dto.tesoreria;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CuentaBancariaTableDto {
    private Long id;
    private Long bancoId;
    private Long tipoCuentaBancariaId;
    private String numeroCuenta;
    private Boolean manejaSobregiro;
    private Boolean aceptaTransferencias;
    private Boolean active;
}
