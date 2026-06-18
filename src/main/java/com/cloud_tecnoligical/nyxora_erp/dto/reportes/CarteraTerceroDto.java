package com.cloud_tecnoligical.nyxora_erp.dto.reportes;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

/** Saldo de cartera (CxC) por cliente, con la porción vencida. */
@Getter
@Setter
public class CarteraTerceroDto {
    private Long clienteId;
    private String clienteNombre;
    private Long documentos;
    private BigDecimal saldoTotal;
    private BigDecimal saldoVencido;
}
