package com.cloud_tecnoligical.nyxora_erp.dto.cartera;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateAcuerdoPagoCuotaDto {

    @NotNull(message = "El valor de la cuota es obligatorio")
    @Positive(message = "El valor de la cuota debe ser positivo")
    private BigDecimal valor;

    private LocalDate fechaAplicacion;
}
