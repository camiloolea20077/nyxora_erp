package com.cloud_tecnoligical.nyxora_erp.dto.producto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductoResponseDto {
    private Long id;
    private Long categoriaId;
    private String codigo;
    private String codigoUnspsc;
    private String nombre;
    private String descripcion;
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
    private Boolean active;
    private LocalDateTime createdAt;
    private List<Long> impuestoIds;
}
