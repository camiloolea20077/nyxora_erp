package com.cloud_tecnoligical.nyxora_erp.dto.presupuesto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateCpcRequestDto extends CreateCpcRequestDto {

    @NotNull(message = "El id es obligatorio")
    private Long id;
}
