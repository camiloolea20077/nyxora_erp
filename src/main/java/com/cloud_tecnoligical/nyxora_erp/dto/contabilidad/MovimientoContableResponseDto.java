package com.cloud_tecnoligical.nyxora_erp.dto.contabilidad;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MovimientoContableResponseDto {
    private Long id;
    private Long comprobanteId;
    private Long cuentaId;
    private Long terceroId;
    private Long centroCostoId;
    private Long proyectoId;
    private Long recursoId;
    private String descripcion;
    private BigDecimal debito;
    private BigDecimal credito;
    private BigDecimal valorBase;
    private Long impuestoId;
    private BigDecimal porcentajeImpuesto;
    private BigDecimal valorTrm;
    private BigDecimal valorDolar;
}
