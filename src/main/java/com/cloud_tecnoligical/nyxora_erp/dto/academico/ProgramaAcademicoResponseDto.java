package com.cloud_tecnoligical.nyxora_erp.dto.academico;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProgramaAcademicoResponseDto {
    private Long id;
    private String codigo;
    private String nombre;
    private String tipoPrograma;
    private String modalidad;
    private Long centroCostoProgramaId;
    private Long centroCostoFacultadId;
    private String registroAcademico;
    private String descripcion;
    private Boolean active;
    private LocalDateTime createdAt;
}
