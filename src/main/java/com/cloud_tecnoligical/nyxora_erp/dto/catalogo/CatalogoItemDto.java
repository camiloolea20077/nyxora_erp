package com.cloud_tecnoligical.nyxora_erp.dto.catalogo;

import lombok.Getter;
import lombok.Setter;

/** Ítem genérico de catálogo (id, codigo, nombre, activo). */
@Getter
@Setter
public class CatalogoItemDto {
    private Long id;
    private String codigo;
    private String nombre;
    private Boolean active;
}
