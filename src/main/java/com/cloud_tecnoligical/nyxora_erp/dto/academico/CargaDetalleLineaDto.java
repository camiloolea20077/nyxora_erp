package com.cloud_tecnoligical.nyxora_erp.dto.academico;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

/** Línea de la carga académica (entrada). */
@Getter
@Setter
public class CargaDetalleLineaDto {
    private Long asignaturaProgramaId;
    private Long grupoAcademicoId;
    private BigDecimal horas;
}
