package com.cloud_tecnoligical.nyxora_erp.dto.categoria;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoriaTableDto {
    private Long id;
    private Long categoriaPadreId;
    private String codigo;
    private String nombre;
    private String tipoProducto;
    private Boolean active;
}
