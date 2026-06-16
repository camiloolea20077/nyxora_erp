package com.cloud_tecnoligical.nyxora_erp.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Getter;
import lombok.Setter;

/** Cuenta bancaria propia de la empresa (R2DBC). */
@Table("cuenta_bancaria")
@Getter
@Setter
public class CuentaBancariaEntity {

    @Id
    private Long id;

    @Column("empresa_id")               private Long empresa_id;
    @Column("banco_id")                 private Long banco_id;
    @Column("tipo_cuenta_bancaria_id")  private Long tipo_cuenta_bancaria_id;
    @Column("numero_cuenta")            private String numero_cuenta;
    @Column("cuenta_contable_id")       private Long cuenta_contable_id;
    @Column("maneja_sobregiro")         private Boolean maneja_sobregiro;
    @Column("acepta_transferencias")    private Boolean acepta_transferencias;
    @Column("fecha_expiracion")         private LocalDate fecha_expiracion;
    @Column("activo")                   private Boolean activo;
    @Column("created_at")               private LocalDateTime created_at;
    @Column("updated_at")               private LocalDateTime updated_at;
    @Column("deleted_at")               private LocalDateTime deleted_at;
    @Column("usuario_creacion")         private Long usuario_creacion;
    @Column("usuario_modificacion")     private Long usuario_modificacion;
}
