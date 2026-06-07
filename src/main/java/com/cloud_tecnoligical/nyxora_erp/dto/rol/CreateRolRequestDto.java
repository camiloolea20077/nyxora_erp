package com.cloud_tecnoligical.nyxora_erp.dto.rol;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateRolRequestDto {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100, message = "El nombre no puede superar 100 caracteres")
    private String name;

    /** Opcional: ids de permisos a asignar. */
    private List<Long> permisoIds;
}
