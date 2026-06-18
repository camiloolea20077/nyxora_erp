package com.cloud_tecnoligical.nyxora_erp.dto.academico;

import java.time.LocalDate;
import java.util.List;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateCargaAcademicaRequestDto {

    @NotNull(message = "La vinculación (docente) es obligatoria")
    private Long vinculacionId;

    private Long nivelEstudioId;
    private String numeroActoAdministrativo;
    private LocalDate fechaActoAdministrativo;
    private List<CargaDetalleLineaDto> detalle;
}
