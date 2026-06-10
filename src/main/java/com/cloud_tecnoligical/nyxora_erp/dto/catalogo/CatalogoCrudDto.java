package com.cloud_tecnoligical.nyxora_erp.dto.catalogo;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/** Alta/edición de un ítem de catálogo plano (id solo en update). */
@Getter
@Setter
public class CatalogoCrudDto {
    private Long id;
    @NotBlank(message = "El código es obligatorio")
    private String codigo;
    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;
    private Boolean activo;
}
