package com.cloud_tecnoligical.nyxora_erp.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Getter;
import lombok.Setter;

/** Acuerdo de pago sobre una cuenta por cobrar (R2DBC). Estados: vigente → cumplido / incumplido / anulado. */
@Table("acuerdo_pago")
@Getter
@Setter
public class AcuerdoPagoEntity {

    @Id
    private Long id;

    @Column("empresa_id")           private Long empresa_id;
    @Column("cuenta_por_cobrar_id") private Long cuenta_por_cobrar_id;
    @Column("fecha")                private LocalDate fecha;
    @Column("numero_cuotas")        private Integer numero_cuotas;
    @Column("estado")               private String estado;
    @Column("activo")               private Boolean activo;
    @Column("created_at")           private LocalDateTime created_at;
    @Column("updated_at")           private LocalDateTime updated_at;
    @Column("deleted_at")           private LocalDateTime deleted_at;
    @Column("usuario_creacion")     private Long usuario_creacion;
    @Column("usuario_modificacion") private Long usuario_modificacion;
}
