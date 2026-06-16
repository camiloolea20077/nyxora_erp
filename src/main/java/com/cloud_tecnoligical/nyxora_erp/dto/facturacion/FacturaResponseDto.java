package com.cloud_tecnoligical.nyxora_erp.dto.facturacion;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FacturaResponseDto {
    private Long id;
    private Long sedeId;
    private Long vigenciaId;
    private Long tipoDocumentoId;
    private Long resolucionDianId;
    private String numero;
    private Long clienteId;
    private Long bodegaId;
    private Long centroCostoId;
    private Long condicionPagoId;
    private LocalDate fecha;
    private LocalDate fechaVencimiento;
    private String observaciones;
    private String estado;
    private BigDecimal subtotal;
    private BigDecimal descuento;
    private BigDecimal impuestos;
    private BigDecimal total;
    private Boolean active;
    private LocalDateTime createdAt;
    private List<FacturaLineaResponseDto> lineas;
}
