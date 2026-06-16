package com.cloud_tecnoligical.nyxora_erp.dto.contratacion;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClausulaPlantillaResponseDto {
    private Long id;
    private String tipoClausula;
    private String numero;
    private String orden;
    private String nombre;
    private String texto;
    private Boolean active;
    private LocalDateTime createdAt;
}
