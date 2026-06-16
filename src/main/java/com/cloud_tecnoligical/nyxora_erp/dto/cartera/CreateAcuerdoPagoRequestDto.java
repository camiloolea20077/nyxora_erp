package com.cloud_tecnoligical.nyxora_erp.dto.cartera;

import java.time.LocalDate;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateAcuerdoPagoRequestDto {

    @NotNull(message = "La cuenta por cobrar es obligatoria")
    private Long cuentaPorCobrarId;

    @NotNull(message = "La fecha es obligatoria")
    private LocalDate fecha;

    @NotEmpty(message = "El acuerdo debe tener al menos una cuota")
    @Valid
    private List<CreateAcuerdoPagoCuotaDto> cuotas;
}
