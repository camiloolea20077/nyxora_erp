package com.cloud_tecnoligical.nyxora_erp.dto.contabilidad;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CuentaTableDto {
    private Long id;
    private Long cuentaPadreId;
    private String codigoCuenta;
    private String nombreCuenta;
    private String naturaleza;
    private Boolean manejaMovimiento;
    private Boolean active;
}
