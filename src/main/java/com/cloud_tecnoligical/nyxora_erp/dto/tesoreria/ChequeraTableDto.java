package com.cloud_tecnoligical.nyxora_erp.dto.tesoreria;

import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChequeraTableDto {
    private Long id;
    private Long cuentaBancariaId;
    private LocalDate fechaExpedicion;
    private Long numeroInicial;
    private Long numeroFinal;
    private Long consecutivoActual;
    private Boolean active;
}
