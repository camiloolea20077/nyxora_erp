package com.cloud_tecnoligical.nyxora_erp.dto.caja;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

/** Aplicación del recibo a una cuenta por cobrar. */
@Getter
@Setter
public class CreateReciboCajaLineaDto {

    @NotNull(message = "La cuenta por cobrar es obligatoria")
    private Long cuentaPorCobrarId;

    @NotNull(message = "El valor aplicado es obligatorio")
    @Positive(message = "El valor aplicado debe ser positivo")
    private BigDecimal valorAplicado;
}
