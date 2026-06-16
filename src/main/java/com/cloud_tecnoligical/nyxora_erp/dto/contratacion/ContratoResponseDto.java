package com.cloud_tecnoligical.nyxora_erp.dto.contratacion;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ContratoResponseDto {
    private Long id;
    private String numero;
    private String nombre;
    private String tipoContrato;
    private Long contratistaId;
    private String contratistaNombre;
    private Long modalidadId;
    private String modalidadNombre;
    private String objeto;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private BigDecimal valor;
    private String estado;
    private Boolean active;
    private LocalDateTime createdAt;

    private List<ContratoClausulaDto> clausulas;
    private List<ContratoPolizaDto> polizas;
}
