package com.cloud_tecnoligical.nyxora_erp.dto.presupuesto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CpcTableDto {
    private Long id;
    private Long cpcPadreId;
    private String codigo;
    private String nombre;
    private Boolean manejaMovimiento;
    private Boolean active;
}
