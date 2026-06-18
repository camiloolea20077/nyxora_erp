package com.cloud_tecnoligical.nyxora_erp.dto.academico;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProgramaAcademicoTableDto {
    private Long id;
    private String codigo;
    private String nombre;
    private String tipoPrograma;
    private String modalidad;
    private Boolean active;
}
