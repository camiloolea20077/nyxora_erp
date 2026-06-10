package com.cloud_tecnoligical.nyxora_erp.dto.producto;

import java.math.BigDecimal;
import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateProductoRequestDto {

    private Long categoriaId;

    @NotBlank(message = "El código es obligatorio")
    @Size(max = 40)
    private String codigo;

    @Size(max = 25)
    private String codigoUnspsc;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 150)
    private String nombre;

    @Size(max = 500)
    private String descripcion;

    @Pattern(regexp = "bien|servicio", message = "El tipo debe ser 'bien' o 'servicio'")
    private String tipo;

    private Boolean esCompuesto;
    private Long unidadMayorId;
    private Long unidadMenorId;
    private BigDecimal contenido;
    private Boolean manejaInventario;
    private Boolean manejaLote;
    private Boolean manejaDesperdicio;
    private Boolean esDevolutivo;
    private BigDecimal stockMinimo;
    private BigDecimal stockMaximo;
    private Integer tiempoReabastecimiento;
    private Long impuestoId;
    private Boolean discriminaIva;
    private Boolean aplicaImpuestoBolsa;
    private BigDecimal tarifaMaxima;
    private Boolean esPos;
    private Long recursoId;

    /** Impuestos adicionales del producto (además del impuesto por defecto). */
    private List<Long> impuestoIds;
}
