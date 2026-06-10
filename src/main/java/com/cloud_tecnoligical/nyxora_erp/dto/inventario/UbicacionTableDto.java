package com.cloud_tecnoligical.nyxora_erp.dto.inventario;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UbicacionTableDto {
    private Long id;
    private Long bodegaId;
    private Long ubicacionPadreId;
    private String codigo;
    private String nombre;
    private Boolean active;
}
