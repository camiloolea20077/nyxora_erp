package com.cloud_tecnoligical.nyxora_erp.dto.organizacion;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CentroCostoTableDto {
    private Long id;
    private Long centroCostoPadreId;
    private String codigo;
    private String nombre;
    private Boolean esObservacion;
    private Boolean active;
}
