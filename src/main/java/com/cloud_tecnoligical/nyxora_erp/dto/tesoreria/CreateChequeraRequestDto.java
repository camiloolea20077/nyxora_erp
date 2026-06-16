package com.cloud_tecnoligical.nyxora_erp.dto.tesoreria;

import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateChequeraRequestDto {

    @NotNull(message = "La cuenta bancaria es obligatoria")
    private Long cuentaBancariaId;

    private LocalDate fechaExpedicion;
    private Long numeroInicial;
    private Long numeroFinal;
}
