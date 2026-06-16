package com.cloud_tecnoligical.nyxora_erp.dto.tesoreria;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateComprobanteEgresoRequestDto {

    private Long cuentaBancariaId;

    @NotNull(message = "El beneficiario es obligatorio")
    private Long beneficiarioId;

    private Long tipoDocumentoId;
    private Long formaPagoId;
    private String numero;

    @NotNull(message = "La fecha es obligatoria")
    private LocalDate fecha;

    @NotNull(message = "El valor es obligatorio")
    @Positive(message = "El valor debe ser positivo")
    private BigDecimal valor;

    private String numeroCheque;
    private String descripcion;

    /** Obligación de pago a la que aplica el egreso (opcional). */
    private Long obligacionPagoId;
}
