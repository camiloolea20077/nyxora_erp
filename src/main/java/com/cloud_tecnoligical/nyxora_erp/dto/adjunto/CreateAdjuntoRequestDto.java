package com.cloud_tecnoligical.nyxora_erp.dto.adjunto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateAdjuntoRequestDto {

    @NotBlank(message = "El módulo es obligatorio")
    @Size(max = 40)
    private String modulo;

    @NotBlank(message = "La entidad es obligatoria")
    @Size(max = 60)
    private String entidad;

    @NotNull(message = "El id de la entidad es obligatorio")
    private Long entidadId;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 255)
    private String nombre;

    @Size(max = 100)
    private String tipoMime;

    @NotBlank(message = "La url es obligatoria")
    @Size(max = 1000)
    private String url;

    private Long tamanoBytes;

    @Size(max = 500)
    private String descripcion;
}
