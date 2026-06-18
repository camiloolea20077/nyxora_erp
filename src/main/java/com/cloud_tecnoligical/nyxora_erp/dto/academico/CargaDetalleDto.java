package com.cloud_tecnoligical.nyxora_erp.dto.academico;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

/** Línea de la carga académica (salida). */
@Getter
@Setter
public class CargaDetalleDto {
    private Long id;
    private Long asignaturaProgramaId;
    private Long grupoAcademicoId;
    private String asignaturaNombre;
    private String grupoNombre;
    private BigDecimal horas;
}
