package com.cloud_tecnoligical.nyxora_erp.dto.caja;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CajaResponseDto {
    private Long id;
    private Long sedeId;
    private Long usuarioId;
    private String codigo;
    private String nombre;
    private String estado;
    private BigDecimal saldoInicial;
    private LocalDateTime fechaApertura;
    private LocalDateTime fechaCierre;
    private Boolean active;
    private LocalDateTime createdAt;
}
