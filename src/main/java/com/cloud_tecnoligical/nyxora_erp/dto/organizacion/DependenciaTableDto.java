package com.cloud_tecnoligical.nyxora_erp.dto.organizacion;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DependenciaTableDto {
    private Long id;
    private Long centroCostoId;
    private Long dependenciaPadreId;
    private String codigo;
    private String nombre;
    private Boolean active;
}
