package com.cloud_tecnoligical.nyxora_erp.dto.inventario;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BodegaResponsableResponseDto {
    private Long id;
    private Long bodegaId;
    private Long terceroId;
    private Boolean predeterminado;
    private Boolean active;
}
