package com.cloud_tecnoligical.nyxora_erp.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Getter;
import lombok.Setter;

/** Encabezado del asiento contable (R2DBC). Estados: borrador → confirmado → reversado. */
@Table("comprobante")
@Getter
@Setter
public class ComprobanteEntity {

    @Id
    private Long id;

    @Column("empresa_id")           private Long empresa_id;
    @Column("periodo_contable_id")  private Long periodo_contable_id;
    @Column("tipo_documento_id")    private Long tipo_documento_id;
    @Column("numero")               private String numero;
    @Column("fecha")                private LocalDate fecha;
    @Column("descripcion")          private String descripcion;
    @Column("estado")               private String estado;          // borrador | confirmado | reversado
    @Column("total_debito")         private BigDecimal total_debito;
    @Column("total_credito")        private BigDecimal total_credito;
    @Column("origen_modulo")        private String origen_modulo;
    @Column("origen_id")            private Long origen_id;
    @Column("activo")               private Boolean activo;
    @Column("created_at")           private LocalDateTime created_at;
    @Column("updated_at")           private LocalDateTime updated_at;
    @Column("deleted_at")           private LocalDateTime deleted_at;
    @Column("usuario_creacion")     private Long usuario_creacion;
    @Column("usuario_modificacion") private Long usuario_modificacion;
}
