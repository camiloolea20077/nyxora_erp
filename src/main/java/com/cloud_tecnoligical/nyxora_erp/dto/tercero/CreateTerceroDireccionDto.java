package com.cloud_tecnoligical.nyxora_erp.dto.tercero;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateTerceroDireccionDto {
    @Pattern(regexp = "principal|facturacion|envio", message = "tipo debe ser principal, facturacion o envio")
    private String tipo;          // default 'principal'
    @NotBlank(message = "La dirección es obligatoria")
    private String direccion;
    private Long municipioId;
    private Long barrioId;
    private String codigoPostal;
    private String telefono;
    private Boolean principal;
}
