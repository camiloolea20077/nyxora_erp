package com.cloud_tecnoligical.nyxora_erp.dto.categoria;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoriaResponseDto {
    private Long id;
    private Long categoriaPadreId;
    private String codigo;
    private String nombre;
    private String tipoProducto;
    private String metodoCosteo;
    private Integer izquierda;
    private Integer derecha;
    private Integer nivel;
    private Boolean active;
    private LocalDateTime createdAt;
}
