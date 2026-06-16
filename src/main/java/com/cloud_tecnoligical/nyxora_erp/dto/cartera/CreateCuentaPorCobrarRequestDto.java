package com.cloud_tecnoligical.nyxora_erp.dto.cartera;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

/** Alta manual de una CxC (saldo inicial). Las CxC de facturas las crea el listener de Facturación. */
@Getter
@Setter
public class CreateCuentaPorCobrarRequestDto {

    @NotNull(message = "El cliente es obligatorio")
    private Long clienteId;

    private Long cuentaId;

    @NotNull(message = "La fecha de emisión es obligatoria")
    private LocalDate fechaEmision;

    private LocalDate fechaVencimiento;
    private Integer dias;

    @NotNull(message = "El valor total es obligatorio")
    @Positive(message = "El valor total debe ser positivo")
    private BigDecimal valorTotal;
}
