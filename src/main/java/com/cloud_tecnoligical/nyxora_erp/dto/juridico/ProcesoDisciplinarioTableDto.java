package com.cloud_tecnoligical.nyxora_erp.dto.juridico;

import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProcesoDisciplinarioTableDto {
    private Long id;
    private LocalDate fecha;
    private String investigadoNombre;
    private String estado;
    private Boolean active;
}
