package com.cloud_tecnoligical.nyxora_erp.dto.academico;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CargaAcademicaResponseDto {
    private Long id;
    private Long vinculacionId;
    private String docenteNombre;
    private Long nivelEstudioId;
    private String numeroActoAdministrativo;
    private LocalDate fechaActoAdministrativo;
    private Boolean active;
    private LocalDateTime createdAt;
    private List<CargaDetalleDto> detalle;
}
