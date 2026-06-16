package com.cloud_tecnoligical.nyxora_erp.dto.tesoreria;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChequeraResponseDto {
    private Long id;
    private Long cuentaBancariaId;
    private LocalDate fechaExpedicion;
    private Long numeroInicial;
    private Long numeroFinal;
    private Long consecutivoActual;
    private Boolean active;
    private LocalDateTime createdAt;
}
