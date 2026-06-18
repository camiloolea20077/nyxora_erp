package com.cloud_tecnoligical.nyxora_erp.dto.reportes;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

/** Línea del balance / estados financieros (una cuenta de movimiento). */
@Getter
@Setter
public class BalanceLineaDto {
    private String clase;          // 1=activo, 2=pasivo, 3=patrimonio, 4=ingreso, 5/6/7=costo/gasto
    private String codigoCuenta;
    private String nombreCuenta;
    private BigDecimal debito;
    private BigDecimal credito;
    private BigDecimal saldo;       // debito - credito (saldo neto)
}
