package com.cloud_tecnoligical.nyxora_erp.dto.rol;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateRolRequestDto {

    @NotNull(message = "El id es obligatorio")
    private Long id;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100, message = "El nombre no puede superar 100 caracteres")
    private String name;

    /** Si viene (aunque sea vacío), reemplaza el set de permisos del rol. Si es null, no se tocan. */
    private List<Long> permisoIds;
}
