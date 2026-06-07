package com.cloud_tecnoligical.nyxora_erp.dto.usuario;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateUsuarioRequestDto {

    @NotNull(message = "El tercero (persona) es obligatorio")
    private Long terceroId;

    @NotBlank(message = "El usuario es obligatorio")
    @Size(max = 60, message = "El usuario no puede superar 60 caracteres")
    private String username;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email no es válido")
    private String email;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 6, max = 100, message = "La contraseña debe tener entre 6 y 100 caracteres")
    private String password;
}
