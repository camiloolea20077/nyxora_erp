package com.cloud_tecnoligical.nyxora_erp.dto.nomina;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateNovedadNominaRequestDto {

    @NotNull(message = "La vinculación es obligatoria")
    private Long vinculacionId;

    @NotNull(message = "El concepto es obligatorio")
    private Long conceptoNominaId;

    private Long terceroId;
    private String descripcion;
    private BigDecimal cantidadValor;
    private LocalDate fechaInicial;
    private LocalDate fechaFinal;
    private LocalDate fechaAplicada;
    private BigDecimal numeroCuota;
    private Integer dias;
    private String tipoAusentismo;
    // embargos
    private String tipoEmbargo;
    private String expediente;
    private String demandante;
    private Long bancoId;
    private String numeroCuentaBancaria;
    private String estadoNovedad;
}
