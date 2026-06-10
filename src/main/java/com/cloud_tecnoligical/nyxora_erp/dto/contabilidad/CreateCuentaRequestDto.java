package com.cloud_tecnoligical.nyxora_erp.dto.contabilidad;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateCuentaRequestDto {

    private Long cuentaPadreId;

    @NotBlank(message = "El código de cuenta es obligatorio")
    @Size(max = 15)
    private String codigoCuenta;

    @NotBlank(message = "El nombre de cuenta es obligatorio")
    @Size(max = 200)
    private String nombreCuenta;

    private Integer nivel;
    private Integer izquierda;
    private Integer derecha;

    @NotBlank(message = "La naturaleza es obligatoria")
    @Pattern(regexp = "debito|credito", message = "La naturaleza debe ser 'debito' o 'credito'")
    private String naturaleza;

    private String tipoCuenta;
    private Boolean manejaMovimiento;
    private Boolean manejaMovimientoManual;
    private Boolean manejaTercero;
    private Boolean manejaCentroCosto;
    private Boolean manejaImpuesto;
    private Boolean manejaProyecto;
    private Boolean manejaRecurso;
    private Boolean manejaSaldoContrario;
    private Boolean esCorriente;
}
