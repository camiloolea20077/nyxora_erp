package com.cloud_tecnoligical.nyxora_erp.dto.impuesto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateImpuestoRequestDto {

    @NotBlank(message = "El código es obligatorio")
    @Size(max = 20)
    private String codigo;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 150)
    private String nombre;

    @NotBlank(message = "El tipo es obligatorio")
    @Pattern(regexp = "iva|retencion|ica|otro", message = "El tipo debe ser 'iva', 'retencion', 'ica' u 'otro'")
    private String tipo;

    private String causacion;
    private String baseGravable;
    private String periodicidad;
    private Boolean aplicaAiu;
    private Boolean retencionNomina;

    @NotNull(message = "La tarifa es obligatoria")
    @DecimalMin(value = "0.0", message = "La tarifa no puede ser negativa")
    private BigDecimal tarifa;

    @NotNull(message = "La vigencia es obligatoria")
    private Long vigenciaId;

    private Long cuentaCompraId;
    private Long cuentaVentaId;
}
