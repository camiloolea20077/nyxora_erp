package com.cloud_tecnoligical.nyxora_erp.dto.academico;

import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CargaAcademicaTableDto {
    private Long id;
    private Long vinculacionId;
    private String docenteNombre;
    private String numeroActoAdministrativo;
    private LocalDate fechaActoAdministrativo;
    private Boolean active;
}
