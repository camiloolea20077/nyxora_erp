package com.cloud_tecnoligical.nyxora_erp.dto.compras;

import java.time.LocalDate;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateRecepcionRequestDto {

    @NotNull(message = "La orden de compra es obligatoria")
    private Long ordenCompraId;

    @NotNull(message = "La bodega es obligatoria")
    private Long bodegaId;

    private Long tipoDocumentoId;
    private String numero;

    @NotNull(message = "La fecha es obligatoria")
    private LocalDate fecha;

    private String observaciones;

    @NotEmpty(message = "La recepción debe tener al menos una línea")
    @Valid
    private List<CreateRecepcionLineaDto> lineas;
}
