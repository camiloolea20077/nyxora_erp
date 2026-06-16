package com.cloud_tecnoligical.nyxora_erp.dto.activosfijos;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateActivoFijoRequestDto {

    @NotBlank(message = "El código es obligatorio")
    private String codigo;

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    private Long productoId;
    private String codigoUnspsc;
    private String codigoBarra;
    private String descripcion;
    private Long marcaId;
    private Long unidadMayorId;
    private String numeroSerie;
    private String modelo;
    private Long bodegaId;
    private Long centroCostoId;
    private Long proveedorId;
    private String numeroFactura;
    private LocalDate fechaFactura;
    private BigDecimal valorCompra;
    private BigDecimal valorSalvamento;
    private BigDecimal porcentajeSalvamento;
    private String metodoDepreciacion;
    private String tipoDepreciacion;
    private BigDecimal deterioro;
    private BigDecimal avaluo;
    private Integer vidaUtil;
    private BigDecimal capitalizado;
    private String estadoActivo;
    private LocalDate fechaSalidaServicio;
}
