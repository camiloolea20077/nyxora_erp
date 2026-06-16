package com.cloud_tecnoligical.nyxora_erp.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Getter;
import lombok.Setter;

/** Comprobante de egreso / pago (R2DBC). Estados: borrador → girado → conciliado / anulado. */
@Table("comprobante_egreso")
@Getter
@Setter
public class ComprobanteEgresoEntity {

    @Id
    private Long id;

    @Column("empresa_id")           private Long empresa_id;
    @Column("cuenta_bancaria_id")   private Long cuenta_bancaria_id;
    @Column("beneficiario_id")      private Long beneficiario_id;
    @Column("tipo_documento_id")    private Long tipo_documento_id;
    @Column("forma_pago_id")        private Long forma_pago_id;
    @Column("numero")               private String numero;
    @Column("fecha")                private LocalDate fecha;
    @Column("valor")                private BigDecimal valor;
    @Column("estado")               private String estado;
    @Column("numero_cheque")        private String numero_cheque;
    @Column("descripcion")          private String descripcion;
    @Column("origen_modulo")        private String origen_modulo;
    @Column("origen_id")            private Long origen_id;
    @Column("activo")               private Boolean activo;
    @Column("created_at")           private LocalDateTime created_at;
    @Column("updated_at")           private LocalDateTime updated_at;
    @Column("deleted_at")           private LocalDateTime deleted_at;
    @Column("usuario_creacion")     private Long usuario_creacion;
    @Column("usuario_modificacion") private Long usuario_modificacion;
}
