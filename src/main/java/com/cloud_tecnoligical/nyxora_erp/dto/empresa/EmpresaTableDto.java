package com.cloud_tecnoligical.nyxora_erp.dto.empresa;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmpresaTableDto {
    private Long id;
    private String nit;
    private String razonSocial;
    private String nombreComercial;
    private Boolean active;
}
