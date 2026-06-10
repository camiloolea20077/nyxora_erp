package com.cloud_tecnoligical.nyxora_erp.dto.producto;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateProductoProveedorDto {

    @NotNull(message = "El proveedor es obligatorio")
    private Long proveedorId;

    @Size(max = 20)
    private String codigoProducto;

    private BigDecimal cantidadMinima;
    private Integer plazoEntrega;
}
