package com.cloud_tecnoligical.nyxora_erp.dto.inventario;

import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoteTableDto {
    private Long id;
    private String codigo;
    private String nombre;
    private LocalDate fechaVencimiento;
    private Boolean active;
}
