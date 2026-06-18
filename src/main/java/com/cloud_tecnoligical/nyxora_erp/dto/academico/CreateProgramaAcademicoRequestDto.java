package com.cloud_tecnoligical.nyxora_erp.dto.academico;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateProgramaAcademicoRequestDto {

    private String codigo;

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    private String tipoPrograma;
    private String modalidad;
    private Long centroCostoProgramaId;
    private Long centroCostoFacultadId;
    private String registroAcademico;
    private String descripcion;
}
