package com.cloud_tecnoligical.nyxora_erp.dto.activosfijos;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DepreciacionTableDto {
    private Long id;
    private LocalDate fechaAplicacion;
    private BigDecimal valorDepreciacion;
    private Integer periodoAmortizacion;
}
