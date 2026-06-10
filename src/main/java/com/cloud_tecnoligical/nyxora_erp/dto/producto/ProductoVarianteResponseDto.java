package com.cloud_tecnoligical.nyxora_erp.dto.producto;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductoVarianteResponseDto {
    private Long id;
    private Long productoId;
    private String skuPlu;
    private String codigoBarra;
    private BigDecimal precioAdicional;
    private BigDecimal costo;
    private Boolean active;
}
