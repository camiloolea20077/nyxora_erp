package com.cloud_tecnoligical.nyxora_erp.dto.recurso;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RecursoTableDto {
    private Long id;
    private String codigo;
    private String nombre;
    private String tipoRecurso;
    private Boolean active;
}
