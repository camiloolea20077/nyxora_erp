package com.cloud_tecnoligical.nyxora_erp.dto.tercero;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TerceroCuentaBancariaResponseDto {
    private Long id;
    private Long terceroId;
    private Long bancoId;
    private Long tipoCuentaBancariaId;
    private String numeroCuenta;
    private Boolean principal;
    private Boolean active;
}
