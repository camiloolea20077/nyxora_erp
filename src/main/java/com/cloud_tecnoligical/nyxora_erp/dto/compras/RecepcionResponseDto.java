package com.cloud_tecnoligical.nyxora_erp.dto.compras;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RecepcionResponseDto {
    private Long id;
    private Long ordenCompraId;
    private Long bodegaId;
    private Long tipoDocumentoId;
    private String numero;
    private LocalDate fecha;
    private String estado;
    private String observaciones;
    private Boolean active;
    private LocalDateTime createdAt;
    private List<RecepcionLineaResponseDto> lineas;
}
