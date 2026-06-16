package com.cloud_tecnoligical.nyxora_erp.dto.facturacion;

import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;

/** Registra/actualiza la metadata de la factura electrónica (CUFE + acuse DIAN). */
@Getter
@Setter
public class RegistrarFacturaDianRequestDto {
    private String cufe;
    private String estadoDian;        // enviada | aceptada | rechazada
    private LocalDate fechaAcuse;
    private String comentarioAcuse;
}
