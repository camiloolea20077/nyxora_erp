package com.cloud_tecnoligical.nyxora_erp.dto.contabilidad;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RecalcularSaldosRequestDto {

    @NotNull(message = "El periodo contable es obligatorio")
    private Long periodoContableId;
}
