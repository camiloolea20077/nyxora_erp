package com.cloud_tecnoligical.nyxora_erp.dto.academico;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/** Traslada la carga docente a nómina: concepto y valor por hora para registrar la novedad. */
@Getter
@Setter
public class GenerarNovedadDocenteRequestDto {

    @NotNull(message = "El concepto de nómina es obligatorio")
    private Long conceptoNominaId;

    @NotNull(message = "El valor por hora es obligatorio")
    private BigDecimal valorHora;
}
