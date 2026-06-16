package com.cloud_tecnoligical.nyxora_erp.dto.contratacion;

import lombok.Getter;
import lombok.Setter;

/** Cláusula de contrato (usada tanto en request como en response). */
@Getter
@Setter
public class ContratoClausulaDto {
    private Long id;
    private String numero;
    private String orden;
    private String nombre;
    private String texto;
}
