package com.cloud_tecnoligical.nyxora_erp.dto.tesoreria;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ComprobanteEgresoTableDto {
    private Long id;
    private Long cuentaBancariaId;
    private Long beneficiarioId;
    private String numero;
    private LocalDate fecha;
    private BigDecimal valor;
    private String estado;
    private Boolean active;
}
