package com.cloud_tecnoligical.nyxora_erp.dto.reportes;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

/** Ejecución presupuestal por rubro (apropiación vs ejecución). */
@Getter
@Setter
public class EjecucionRubroDto {
    private Long rubroId;
    private String codigoRubro;
    private String nombreRubro;
    private String tipoRubro;
    private BigDecimal apropiacion;
    private BigDecimal comprometido;
    private BigDecimal obligado;
    private BigDecimal pagado;
    private BigDecimal disponible;   // apropiacion - comprometido
}
