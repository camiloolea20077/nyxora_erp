package com.cloud_tecnoligical.nyxora_erp.dto.catalogo;

import lombok.Getter;
import lombok.Setter;

/** Ubicación geográfica de un municipio (para preseleccionar el cascade país→depto→municipio). */
@Getter
@Setter
public class UbicacionMunicipioDto {
    private Long municipioId;
    private Long departamentoId;
    private Long paisId;
}
