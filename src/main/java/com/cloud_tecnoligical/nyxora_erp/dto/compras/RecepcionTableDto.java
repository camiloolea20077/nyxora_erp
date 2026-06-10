package com.cloud_tecnoligical.nyxora_erp.dto.compras;

import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RecepcionTableDto {
    private Long id;
    private Long ordenCompraId;
    private String numero;
    private LocalDate fecha;
    private String estado;
    private Boolean active;
}
