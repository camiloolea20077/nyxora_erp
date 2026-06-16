package com.cloud_tecnoligical.nyxora_erp.dto.facturacion;

import lombok.Getter;
import lombok.Setter;

/**
 * Parámetros de emisión. Si se envían las cuentas + periodo, se publica el asiento contable
 * (débito CxC cliente / crédito ingreso + crédito IVA) por el bus de eventos; si no, solo
 * se numera y sale de inventario.
 */
@Getter
@Setter
public class EmitirFacturaRequestDto {
    private Long cuentaClienteId;     // CxC — débito por el total
    private Long cuentaIngresoId;     // ingreso — crédito por el subtotal
    private Long cuentaImpuestoId;    // IVA por pagar — crédito por los impuestos (si aplica)
    private Long periodoContableId;
}
