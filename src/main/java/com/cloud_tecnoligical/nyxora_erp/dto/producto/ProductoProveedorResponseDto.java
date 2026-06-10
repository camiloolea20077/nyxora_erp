package com.cloud_tecnoligical.nyxora_erp.dto.producto;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductoProveedorResponseDto {
    private Long id;
    private Long productoId;
    private Long proveedorId;
    private String codigoProducto;
    private BigDecimal cantidadMinima;
    private Integer plazoEntrega;
    private Boolean active;
}
