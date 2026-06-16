package com.cloud_tecnoligical.nyxora_erp.dto.activosfijos;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ActivoFijoResponseDto {
    private Long id;
    private Long productoId;
    private String codigo;
    private String codigoUnspsc;
    private String codigoBarra;
    private String nombre;
    private String descripcion;
    private Long marcaId;
    private Long unidadMayorId;
    private String numeroSerie;
    private String modelo;
    private Long bodegaId;
    private Long centroCostoId;
    private Long proveedorId;
    private String proveedorNombre;
    private String numeroFactura;
    private LocalDate fechaFactura;
    private BigDecimal valorCompra;
    private BigDecimal valorSalvamento;
    private BigDecimal porcentajeSalvamento;
    private String metodoDepreciacion;
    private String tipoDepreciacion;
    private BigDecimal valorDepreciacion;
    private BigDecimal deterioro;
    private BigDecimal valorActual;
    private BigDecimal avaluo;
    private Integer vidaUtil;
    private Integer mesesDepreciados;
    private BigDecimal capitalizado;
    private String estadoActivo;
    private LocalDate fechaSalidaServicio;
    private Boolean active;
    private LocalDateTime createdAt;

    private List<ActivoFijoResponsableDto> responsables;
    private List<ActivoFijoPolizaDto> polizas;
}
