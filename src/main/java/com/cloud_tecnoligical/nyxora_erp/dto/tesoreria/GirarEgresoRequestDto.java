package com.cloud_tecnoligical.nyxora_erp.dto.tesoreria;

import lombok.Getter;
import lombok.Setter;

/**
 * Parámetros de giro. Si se envían las cuentas + periodo se publica el asiento contable
 * (débito CxP / crédito banco) por el bus de eventos.
 */
@Getter
@Setter
public class GirarEgresoRequestDto {
    private Long cuentaBancoId;
    private Long cuentaCxpId;
    private Long periodoContableId;
}
