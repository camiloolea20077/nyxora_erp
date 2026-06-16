package com.cloud_tecnoligical.nyxora_erp.dto.activosfijos;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateDepreciacionRequestDto {

    @NotNull(message = "El activo fijo es obligatorio")
    private Long activoFijoId;

    @NotNull(message = "La fecha de aplicación es obligatoria")
    private LocalDate fechaAplicacion;

    @NotNull(message = "El valor de depreciación es obligatorio")
    private BigDecimal valorDepreciacion;

    private BigDecimal cuotaDepreciacion;
    private Integer periodoAmortizacion;
    private Integer unidadesProducidas;
}
