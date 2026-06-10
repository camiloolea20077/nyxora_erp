package com.cloud_tecnoligical.nyxora_erp.dto.organizacion;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DependenciaResponseDto {
    private Long id;
    private Long centroCostoId;
    private Long dependenciaPadreId;
    private String codigo;
    private String nombre;
    private String ubicacion;
    private String latitud;
    private String longitud;
    private Integer izquierda;
    private Integer derecha;
    private Integer nivel;
    private Boolean active;
    private LocalDateTime createdAt;
}
