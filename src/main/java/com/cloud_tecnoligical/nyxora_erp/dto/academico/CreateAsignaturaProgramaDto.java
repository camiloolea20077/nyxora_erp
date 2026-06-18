package com.cloud_tecnoligical.nyxora_erp.dto.academico;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateAsignaturaProgramaDto {
    @NotNull(message = "El programa es obligatorio")
    private Long programaAcademicoId;
    private Integer semestre;
    private Integer creditos;
}
