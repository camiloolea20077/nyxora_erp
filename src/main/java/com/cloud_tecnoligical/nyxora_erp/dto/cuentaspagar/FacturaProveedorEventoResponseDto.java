package com.cloud_tecnoligical.nyxora_erp.dto.cuentaspagar;

import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FacturaProveedorEventoResponseDto {
    private Long id;
    private Long facturaProveedorId;
    private String evento;
    private LocalDate fechaEvento;
    private String cudeEvento;
    private String conceptoReclamo;
    private String descripcionReclamo;
    private String estado;
    private String errorEvento;
}
