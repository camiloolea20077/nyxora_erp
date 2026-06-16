package com.cloud_tecnoligical.nyxora_erp.dto.activosfijos;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DepreciacionResponseDto {
    private Long id;
    private Long activoFijoId;
    private LocalDate fechaAplicacion;
    private BigDecimal valorDepreciacion;
    private BigDecimal cuotaDepreciacion;
    private Integer periodoAmortizacion;
    private Integer unidadesProducidas;
    private LocalDateTime createdAt;
}
