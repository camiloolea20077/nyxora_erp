package com.cloud_tecnoligical.nyxora_erp.dto.tercero;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateTerceroContactoDto {
    @NotBlank(message = "El nombre del contacto es obligatorio")
    private String nombre;
    private String cargo;
    private String telefono;
    private String celular;
    @Email(message = "El email no es válido")
    private String email;
    private String notas;
    private Boolean principal;
}
