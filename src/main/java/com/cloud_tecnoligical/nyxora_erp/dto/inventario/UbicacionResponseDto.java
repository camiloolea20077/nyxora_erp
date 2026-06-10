package com.cloud_tecnoligical.nyxora_erp.dto.inventario;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UbicacionResponseDto {
    private Long id;
    private Long bodegaId;
    private Long ubicacionPadreId;
    private String codigo;
    private String nombre;
    private Integer pasillo;
    private Integer altura;
    private Integer posicion;
    private Integer izquierda;
    private Integer derecha;
    private Integer nivel;
    private Boolean active;
    private LocalDateTime createdAt;
}
