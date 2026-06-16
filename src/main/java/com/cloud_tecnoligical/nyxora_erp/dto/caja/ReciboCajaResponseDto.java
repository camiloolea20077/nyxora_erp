package com.cloud_tecnoligical.nyxora_erp.dto.caja;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReciboCajaResponseDto {
    private Long id;
    private Long cajaId;
    private Long tipoDocumentoId;
    private String numero;
    private Long clienteId;
    private LocalDate fecha;
    private BigDecimal valor;
    private String estado;
    private String observaciones;
    private Boolean active;
    private LocalDateTime createdAt;
    private List<ReciboCajaPagoResponseDto> pagos;
    private List<ReciboCajaLineaResponseDto> lineas;
}
