package com.cloud_tecnoligical.nyxora_erp.dto.documento;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TipoDocumentoResponseDto {
    private Long id;
    private String modulo;
    private String codigo;
    private String nombre;
    private String prefijo;
    private Boolean reiniciaPorVigencia;
    private Boolean active;
    private LocalDateTime createdAt;
}
