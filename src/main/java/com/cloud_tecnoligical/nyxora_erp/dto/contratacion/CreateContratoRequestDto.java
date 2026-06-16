package com.cloud_tecnoligical.nyxora_erp.dto.contratacion;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateContratoRequestDto {

    @NotBlank(message = "El nombre del contrato es obligatorio")
    private String nombre;

    private String numero;
    private String tipoContrato;
    private Long contratistaId;
    private Long modalidadId;
    private String objeto;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private BigDecimal valor;

    @Valid
    private List<ContratoClausulaDto> clausulas;
}
