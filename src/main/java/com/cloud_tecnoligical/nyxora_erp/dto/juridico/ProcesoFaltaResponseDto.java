package com.cloud_tecnoligical.nyxora_erp.dto.juridico;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProcesoFaltaResponseDto {
    private Long id;
    private Long faltaId;
    private String faltaCodigo;
    private String faltaNombre;
}
