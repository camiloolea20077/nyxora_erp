package com.cloud_tecnoligical.nyxora_erp.dto.contratacion;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateClausulaPlantillaRequestDto {

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    private String tipoClausula;
    private String numero;
    private String orden;
    private String texto;
}
