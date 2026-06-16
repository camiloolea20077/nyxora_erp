package com.cloud_tecnoligical.nyxora_erp.dto.facturacion;

import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResolucionDianTableDto {
    private Long id;
    private String numeroResolucion;
    private String prefijo;
    private Long facturaInicial;
    private Long facturaFinal;
    private LocalDate fechaFinal;
    private Long consecutivoActual;
    private Boolean active;
}
