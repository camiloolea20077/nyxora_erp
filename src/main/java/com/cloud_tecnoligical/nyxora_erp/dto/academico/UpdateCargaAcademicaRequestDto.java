package com.cloud_tecnoligical.nyxora_erp.dto.academico;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateCargaAcademicaRequestDto extends CreateCargaAcademicaRequestDto {
    @NotNull(message = "El id es obligatorio")
    private Long id;
    private Boolean active;
}
