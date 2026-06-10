package com.cloud_tecnoligical.nyxora_erp.dto.inventario;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;

/** Renglón de kardex: el movimiento + el saldo corriente acumulado. */
@Getter
@Setter
public class KardexItemDto {
    private Long id;
    private Long bodegaId;
    private Long productoId;
    private Long productoVarianteId;
    private Long loteId;
    private String tipo;
    private LocalDate fecha;
    private BigDecimal cantidad;
    private BigDecimal costoUnitario;
    private String descripcion;
    private BigDecimal saldoCorriente;
}
