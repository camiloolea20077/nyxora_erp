package com.cloud_tecnoligical.nyxora_erp.dto.juridico;

import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateProcesoDescargoDto {
    private LocalDate fecha;
    private String texto;
}
