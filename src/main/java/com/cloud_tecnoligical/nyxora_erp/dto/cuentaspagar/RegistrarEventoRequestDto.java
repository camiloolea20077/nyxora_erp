package com.cloud_tecnoligical.nyxora_erp.dto.cuentaspagar;

import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;

/** Evento RADIAN sobre una factura de proveedor (acuse, reclamo, aceptación, etc.). */
@Getter
@Setter
public class RegistrarEventoRequestDto {
    private String evento;
    private LocalDate fechaEvento;
    private String cudeEvento;
    private String conceptoReclamo;
    private String descripcionReclamo;
    private String estado;
}
