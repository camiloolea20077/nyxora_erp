package com.cloud_tecnoligical.nyxora_erp.dto.juridico;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateProcesoDisciplinarioRequestDto extends CreateProcesoDisciplinarioRequestDto {
    @NotNull(message = "El id es obligatorio")
    private Long id;
    private Boolean active;
}
