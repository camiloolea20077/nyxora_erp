package com.cloud_tecnoligical.nyxora_erp.dto.contabilidad;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CuentaResponseDto {
    private Long id;
    private Long cuentaPadreId;
    private String codigoCuenta;
    private String nombreCuenta;
    private Integer nivel;
    private Integer izquierda;
    private Integer derecha;
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
    private Boolean active;
    private LocalDateTime createdAt;
}
