package com.cloud_tecnoligical.nyxora_erp.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Getter;
import lombok.Setter;

/** Obligación de pago / cuenta por pagar (R2DBC). Estados: pendiente, parcial, pagada, anulada. */
@Table("obligacion_pago")
@Getter
@Setter
public class ObligacionPagoEntity {

    @Id
    private Long id;

    @Column("empresa_id")           private Long empresa_id;
    @Column("proveedor_id")         private Long proveedor_id;
    @Column("factura_proveedor_id") private Long factura_proveedor_id;
    @Column("cuenta_id")            private Long cuenta_id;
    @Column("numero")               private String numero;
    @Column("fecha")                private LocalDate fecha;
    @Column("fecha_vencimiento")    private LocalDate fecha_vencimiento;
    @Column("valor_total")          private BigDecimal valor_total;
    @Column("saldo")                private BigDecimal saldo;
    @Column("estado")               private String estado;
    @Column("activo")               private Boolean activo;
    @Column("created_at")           private LocalDateTime created_at;
    @Column("updated_at")           private LocalDateTime updated_at;
    @Column("deleted_at")           private LocalDateTime deleted_at;
    @Column("usuario_creacion")     private Long usuario_creacion;
    @Column("usuario_modificacion") private Long usuario_modificacion;
}
