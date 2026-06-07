package com.cloud_tecnoligical.nyxora_erp.dto.parametro;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateParametroRequestDto {

    @NotBlank(message = "La clave es obligatoria")
    @Size(max = 100, message = "La clave no puede superar 100 caracteres")
    private String key;

    @NotBlank(message = "El valor es obligatorio")
    @Size(max = 500, message = "El valor no puede superar 500 caracteres")
    private String value;

    @Pattern(regexp = "string|int|decimal|bool|date", message = "tipo de dato inválido")
    private String dataType;   // por defecto 'string'
}
