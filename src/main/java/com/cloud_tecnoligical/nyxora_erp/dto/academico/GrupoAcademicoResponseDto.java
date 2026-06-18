package com.cloud_tecnoligical.nyxora_erp.dto.academico;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GrupoAcademicoResponseDto {
    private Long id;
    private Long programaAcademicoId;
    private String codigo;
    private String nombre;
    private String periodo;
    private Boolean active;
    private LocalDateTime createdAt;
}
