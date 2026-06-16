package com.cloud_tecnoligical.nyxora_erp.dto.cartera;

import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AcuerdoPagoTableDto {
    private Long id;
    private Long cuentaPorCobrarId;
    private LocalDate fecha;
    private Integer numeroCuotas;
    private String estado;
    private Boolean active;
}
