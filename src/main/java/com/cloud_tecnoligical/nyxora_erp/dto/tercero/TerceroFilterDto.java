package com.cloud_tecnoligical.nyxora_erp.dto.tercero;

import lombok.Getter;
import lombok.Setter;

/**
 * Filtros avanzados del listado de terceros (params del PageableDto).
 * tipoTerceroId filtra por la clasificación (cliente/proveedor/empleado); los demás por campo.
 */
@Getter
@Setter
public class TerceroFilterDto {
    private Long tipoTerceroId;
    private String numeroDocumento;
    private String tipoPersona;
}
