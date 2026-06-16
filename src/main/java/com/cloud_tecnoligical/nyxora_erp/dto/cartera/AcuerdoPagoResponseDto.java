package com.cloud_tecnoligical.nyxora_erp.dto.cartera;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AcuerdoPagoResponseDto {
    private Long id;
    private Long cuentaPorCobrarId;
    private LocalDate fecha;
    private Integer numeroCuotas;
    private String estado;
    private Boolean active;
    private LocalDateTime createdAt;
    private List<AcuerdoPagoCuotaResponseDto> cuotas;
}
