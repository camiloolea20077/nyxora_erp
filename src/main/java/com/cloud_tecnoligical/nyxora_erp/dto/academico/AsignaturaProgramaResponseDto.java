package com.cloud_tecnoligical.nyxora_erp.dto.academico;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AsignaturaProgramaResponseDto {
    private Long id;
    private Long asignaturaId;
    private Long programaAcademicoId;
    private String programaNombre;
    private Integer semestre;
    private Integer creditos;
    private Boolean active;
}
