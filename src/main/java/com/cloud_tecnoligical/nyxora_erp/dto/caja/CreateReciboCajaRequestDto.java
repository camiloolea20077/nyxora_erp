package com.cloud_tecnoligical.nyxora_erp.dto.caja;

import java.time.LocalDate;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateReciboCajaRequestDto {

    @NotNull(message = "La caja es obligatoria")
    private Long cajaId;

    private Long tipoDocumentoId;
    private String numero;
    private Long clienteId;

    @NotNull(message = "La fecha es obligatoria")
    private LocalDate fecha;

    private String observaciones;

    @NotEmpty(message = "El recibo debe tener al menos un medio de pago")
    @Valid
    private List<CreateReciboCajaPagoDto> pagos;

    /** Aplicación a cuentas por cobrar (opcional; puede ser un recibo sin aplicar). */
    @Valid
    private List<CreateReciboCajaLineaDto> lineas;

    // Cuentas/periodo opcionales para el asiento contable (débito caja / crédito CxC).
    private Long cuentaCajaId;
    private Long cuentaCxcId;
    private Long periodoContableId;
}
