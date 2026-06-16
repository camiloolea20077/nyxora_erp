package com.cloud_tecnoligical.nyxora_erp.dto.cuentaspagar;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateObligacionPagoRequestDto {

    @NotNull(message = "El proveedor es obligatorio")
    private Long proveedorId;

    private Long facturaProveedorId;
    private Long cuentaId;
    private String numero;

    @NotNull(message = "La fecha es obligatoria")
    private LocalDate fecha;

    private LocalDate fechaVencimiento;

    @NotNull(message = "El valor total es obligatorio")
    @Positive(message = "El valor total debe ser positivo")
    private BigDecimal valorTotal;

    @Valid
    private List<CreateObligacionPagoRetencionDto> retenciones;
}
