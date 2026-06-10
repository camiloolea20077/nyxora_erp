package com.cloud_tecnoligical.nyxora_erp.dto.inventario;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateLoteRequestDto {

    private Long productoVarianteId;

    @NotBlank(message = "El código es obligatorio")
    @Size(max = 20)
    private String codigo;

    @Size(max = 150)
    private String nombre;

    private LocalDate fechaFabricado;
    private LocalDate fechaVencimiento;
}
