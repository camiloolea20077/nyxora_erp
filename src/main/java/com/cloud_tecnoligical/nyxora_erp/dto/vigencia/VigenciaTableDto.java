package com.cloud_tecnoligical.nyxora_erp.dto.vigencia;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VigenciaTableDto {
    private Long id;
    private Integer year;
    private String status;
    private Boolean active;
}
