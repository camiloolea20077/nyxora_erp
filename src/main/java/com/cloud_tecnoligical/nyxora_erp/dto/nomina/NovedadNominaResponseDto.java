package com.cloud_tecnoligical.nyxora_erp.dto.nomina;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NovedadNominaResponseDto {
    private Long id;
    private Long vinculacionId;
    private Long conceptoNominaId;
    private String conceptoNombre;
    private Long terceroId;
    private String descripcion;
    private BigDecimal cantidadValor;
    private LocalDate fechaInicial;
    private LocalDate fechaFinal;
    private LocalDate fechaAplicada;
    private BigDecimal numeroCuota;
    private Integer dias;
    private String tipoAusentismo;
    private String tipoEmbargo;
    private String expediente;
    private String demandante;
    private Long bancoId;
    private String numeroCuentaBancaria;
    private String estadoNovedad;
    private Boolean anulado;
    private Boolean active;
    private LocalDateTime createdAt;
}
