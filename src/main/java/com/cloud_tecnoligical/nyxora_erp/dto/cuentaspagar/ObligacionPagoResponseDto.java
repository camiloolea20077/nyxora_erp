package com.cloud_tecnoligical.nyxora_erp.dto.cuentaspagar;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ObligacionPagoResponseDto {
    private Long id;
    private Long proveedorId;
    private Long facturaProveedorId;
    private Long cuentaId;
    private String numero;
    private LocalDate fecha;
    private LocalDate fechaVencimiento;
    private BigDecimal valorTotal;
    private BigDecimal saldo;
    private String estado;
    private Boolean active;
    private LocalDateTime createdAt;
    private List<ObligacionPagoRetencionResponseDto> retenciones;
}
