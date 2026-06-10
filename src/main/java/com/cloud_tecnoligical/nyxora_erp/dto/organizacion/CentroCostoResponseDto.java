package com.cloud_tecnoligical.nyxora_erp.dto.organizacion;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CentroCostoResponseDto {
    private Long id;
    private Long sedeId;
    private Long centroCostoPadreId;
    private String codigo;
    private String nombre;
    private String tipoCentroCosto;
    private String claseCentroCosto;
    private Boolean esObservacion;
    private Boolean manejaPlanFinanciero;
    private Long terceroId;
    private String direccion;
    private Long unidadNegocioId;
    private Integer izquierda;
    private Integer derecha;
    private Integer nivel;
    private Boolean active;
    private LocalDateTime createdAt;
}
