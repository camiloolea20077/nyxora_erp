package com.cloud_tecnoligical.nyxora_erp.dto.caja;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateArqueoRequestDto {

    @NotNull(message = "La caja es obligatoria")
    private Long cajaId;

    @NotNull(message = "El valor declarado es obligatorio")
    private BigDecimal valorDeclarado;

    private String observaciones;
}
