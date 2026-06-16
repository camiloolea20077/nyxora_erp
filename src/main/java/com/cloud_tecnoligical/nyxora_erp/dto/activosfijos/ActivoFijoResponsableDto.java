package com.cloud_tecnoligical.nyxora_erp.dto.activosfijos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ActivoFijoResponsableDto {
    private Long id;
    private Long terceroId;
    private String terceroNombre;
    private Boolean active;
}
