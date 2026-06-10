package com.cloud_tecnoligical.nyxora_erp.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Getter;
import lombok.Setter;

/**
 * Impuesto/retención (R2DBC, ← com_impuestos_deducciones). Tarifa vigente por vigencia.
 * Las cuentas contables se enlazan en la fase de Contabilidad.
 */
@Table("impuesto")
@Getter
@Setter
public class ImpuestoEntity {

    @Id
    private Long id;

    @Column("empresa_id")           private Long empresa_id;
    @Column("codigo")               private String codigo;
    @Column("nombre")               private String nombre;
    @Column("tipo")                 private String tipo;            // iva | retencion | ica | otro
    @Column("causacion")            private String causacion;
    @Column("base_gravable")        private String base_gravable;
    @Column("periodicidad")         private String periodicidad;
    @Column("aplica_aiu")           private Boolean aplica_aiu;
    @Column("retencion_nomina")     private Boolean retencion_nomina;
    @Column("tarifa")               private BigDecimal tarifa;
    @Column("vigencia_id")          private Long vigencia_id;
    @Column("cuenta_compra_id")     private Long cuenta_compra_id;
    @Column("cuenta_venta_id")      private Long cuenta_venta_id;
    @Column("activo")               private Boolean activo;
    @Column("created_at")           private LocalDateTime created_at;
    @Column("updated_at")           private LocalDateTime updated_at;
    @Column("deleted_at")           private LocalDateTime deleted_at;
    @Column("usuario_creacion")     private Long usuario_creacion;
    @Column("usuario_modificacion") private Long usuario_modificacion;
}
