package com.cloud_tecnoligical.nyxora_erp.dto.inventario;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MarcaTableDto {
    private Long id;
    private String codigo;
    private String nombre;
    private Boolean active;
}
