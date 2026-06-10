package com.cloud_tecnoligical.nyxora_erp.dto.contabilidad;

import java.time.LocalDate;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateComprobanteRequestDto {

    @NotNull(message = "El periodo contable es obligatorio")
    private Long periodoContableId;

    private Long tipoDocumentoId;
    private String numero;

    @NotNull(message = "La fecha es obligatoria")
    private LocalDate fecha;

    private String descripcion;
    private String origenModulo;
    private Long origenId;

    @NotEmpty(message = "El comprobante debe tener al menos un movimiento")
    @Valid
    private List<CreateMovimientoContableDto> movimientos;
}
