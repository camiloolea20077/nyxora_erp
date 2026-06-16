package com.cloud_tecnoligical.nyxora_erp.dto.tesoreria;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ComprobanteEgresoResponseDto {
    private Long id;
    private Long cuentaBancariaId;
    private Long beneficiarioId;
    private Long tipoDocumentoId;
    private Long formaPagoId;
    private String numero;
    private LocalDate fecha;
    private BigDecimal valor;
    private String estado;
    private String numeroCheque;
    private String descripcion;
    private String origenModulo;
    private Long origenId;
    private Boolean active;
    private LocalDateTime createdAt;
}
