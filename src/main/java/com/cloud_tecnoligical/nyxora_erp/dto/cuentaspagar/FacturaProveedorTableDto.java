package com.cloud_tecnoligical.nyxora_erp.dto.cuentaspagar;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FacturaProveedorTableDto {
    private Long id;
    private Long proveedorId;
    private String numeroDocumento;
    private LocalDate fechaRecepcion;
    private BigDecimal valorFactura;
    private String estado;
    private Boolean active;
}
