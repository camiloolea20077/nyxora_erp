package com.cloud_tecnoligical.nyxora_erp.dto.juridico;

import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateProcesoDisciplinarioRequestDto {

    @NotNull(message = "La fecha es obligatoria")
    private LocalDate fecha;

    private Long vinculacionId;
    private Long responsableId;
    private String descripcion;
}
