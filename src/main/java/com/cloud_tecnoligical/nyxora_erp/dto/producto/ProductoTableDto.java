package com.cloud_tecnoligical.nyxora_erp.dto.producto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductoTableDto {
    private Long id;
    private String codigo;
    private String nombre;
    private String tipo;
    private Long categoriaId;
    private Boolean active;
}
