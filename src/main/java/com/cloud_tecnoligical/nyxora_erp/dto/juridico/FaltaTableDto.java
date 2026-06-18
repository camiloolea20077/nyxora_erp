package com.cloud_tecnoligical.nyxora_erp.dto.juridico;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FaltaTableDto {
    private Long id;
    private String codigo;
    private String nombre;
    private Boolean active;
}
