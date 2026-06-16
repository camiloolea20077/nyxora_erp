package com.cloud_tecnoligical.nyxora_erp.dto.facturacion;

import java.time.LocalDate;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateFacturaRequestDto {

    private Long sedeId;
    private Long vigenciaId;
    private Long tipoDocumentoId;
    private Long resolucionDianId;
    private String numero;

    @NotNull(message = "El cliente es obligatorio")
    private Long clienteId;

    private Long bodegaId;
    private Long centroCostoId;
    private Long condicionPagoId;

    @NotNull(message = "La fecha es obligatoria")
    private LocalDate fecha;

    private LocalDate fechaVencimiento;
    private String observaciones;

    @NotEmpty(message = "La factura debe tener al menos una línea")
    @Valid
    private List<CreateFacturaLineaDto> lineas;
}
