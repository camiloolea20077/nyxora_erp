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
public class CreateOrdenCompraRequestDto {

    private Long sedeId;
    private Long vigenciaId;
    private Long tipoDocumentoId;
    private String numero;

    @NotNull(message = "El proveedor es obligatorio")
    private Long proveedorId;

    private Long bodegaId;
    private Long centroCostoId;
    private Long condicionPagoId;

    @NotNull(message = "La fecha es obligatoria")
    private LocalDate fecha;

    private LocalDate fechaEntrega;
    private String observaciones;

    @NotEmpty(message = "La orden debe tener al menos una línea")
    @Valid
    private List<CreateOrdenCompraLineaDto> lineas;
}
