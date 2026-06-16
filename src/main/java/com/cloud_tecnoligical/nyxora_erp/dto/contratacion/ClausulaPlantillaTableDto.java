package com.cloud_tecnoligical.nyxora_erp.dto.contratacion;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClausulaPlantillaTableDto {
    private Long id;
    private String tipoClausula;
    private String numero;
    private String nombre;
    private Boolean active;
}
