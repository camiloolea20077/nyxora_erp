package com.cloud_tecnoligical.nyxora_erp.dto.adjunto;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdjuntoResponseDto {
    private Long id;
    private String modulo;
    private String entidad;
    private Long entidadId;
    private String nombre;
    private String tipoMime;
    private String url;
    private Long tamanoBytes;
    private String descripcion;
    private Boolean active;
    private LocalDateTime createdAt;
}
