package com.cloud_tecnoligical.nyxora_erp.dto.impuesto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ImpuestoResponseDto {
    private Long id;
    private String codigo;
    private String nombre;
    private String tipo;
    private String causacion;
    private String baseGravable;
    private String periodicidad;
    private Boolean aplicaAiu;
    private Boolean retencionNomina;
    private BigDecimal tarifa;
    private Long vigenciaId;
    private Long cuentaCompraId;
    private Long cuentaVentaId;
    private Boolean active;
    private LocalDateTime createdAt;
}
