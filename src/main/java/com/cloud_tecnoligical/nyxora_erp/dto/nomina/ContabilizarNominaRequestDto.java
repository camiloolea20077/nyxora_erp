package com.cloud_tecnoligical.nyxora_erp.dto.nomina;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/** Cuentas y periodo para generar el asiento contable de la nómina (interfaz contable por evento). */
@Getter
@Setter
public class ContabilizarNominaRequestDto {

    @NotNull(message = "El periodo contable es obligatorio")
    private Long periodoContableId;

    @NotNull(message = "La cuenta de gasto es obligatoria")
    private Long cuentaGastoId;

    @NotNull(message = "La cuenta por pagar es obligatoria")
    private Long cuentaPorPagarId;

    /** Cuenta para las deducciones (descuentos al empleado). Si es null, se netean contra la cuenta por pagar. */
    private Long cuentaDeduccionesId;
}
