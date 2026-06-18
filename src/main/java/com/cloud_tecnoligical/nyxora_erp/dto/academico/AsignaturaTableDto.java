package com.cloud_tecnoligical.nyxora_erp.dto.academico;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AsignaturaTableDto {
    private Long id;
    private String codigo;
    private String nombre;
    private Boolean active;
}
