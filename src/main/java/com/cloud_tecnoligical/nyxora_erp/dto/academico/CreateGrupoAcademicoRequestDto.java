package com.cloud_tecnoligical.nyxora_erp.dto.academico;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateGrupoAcademicoRequestDto {
    private Long programaAcademicoId;

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    private String codigo;
    private String periodo;
}
