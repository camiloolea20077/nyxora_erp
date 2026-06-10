package com.cloud_tecnoligical.nyxora_erp.dto.producto;

import java.math.BigDecimal;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateProductoVarianteDto {

    @Size(max = 15)
    private String skuPlu;

    @Size(max = 13)
    private String codigoBarra;

    private BigDecimal precioAdicional;
    private BigDecimal costo;
}
