package com.cloud_tecnoligical.nyxora_erp.dto.compras;

import lombok.Getter;
import lombok.Setter;

/**
 * Parámetros de confirmación. Si se envían las 3 cuentas/periodo, se publica el asiento contable
 * (débito inventario / crédito contrapartida) por el bus de eventos; si no, solo entra a inventario.
 */
@Getter
@Setter
public class ConfirmarRecepcionRequestDto {
    private Long cuentaInventarioId;
    private Long cuentaContrapartidaId;
    private Long periodoContableId;
}
