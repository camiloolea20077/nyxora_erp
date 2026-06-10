package com.cloud_tecnoligical.nyxora_erp.dto.contabilidad;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ComprobanteResponseDto {
    private Long id;
    private Long periodoContableId;
    private Long tipoDocumentoId;
    private String numero;
    private LocalDate fecha;
    private String descripcion;
    private String estado;
    private BigDecimal totalDebito;
    private BigDecimal totalCredito;
    private String origenModulo;
    private Long origenId;
    private Boolean active;
    private LocalDateTime createdAt;
    private List<MovimientoContableResponseDto> movimientos;
}
