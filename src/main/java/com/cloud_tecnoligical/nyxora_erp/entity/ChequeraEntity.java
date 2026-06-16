package com.cloud_tecnoligical.nyxora_erp.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Getter;
import lombok.Setter;

/** Chequera asociada a una cuenta bancaria (R2DBC). */
@Table("chequera")
@Getter
@Setter
public class ChequeraEntity {

    @Id
    private Long id;

    @Column("empresa_id")           private Long empresa_id;
    @Column("cuenta_bancaria_id")   private Long cuenta_bancaria_id;
    @Column("fecha_expedicion")     private LocalDate fecha_expedicion;
    @Column("numero_inicial")       private Long numero_inicial;
    @Column("numero_final")         private Long numero_final;
    @Column("consecutivo_actual")   private Long consecutivo_actual;
    @Column("activo")               private Boolean activo;
    @Column("created_at")           private LocalDateTime created_at;
    @Column("updated_at")           private LocalDateTime updated_at;
    @Column("deleted_at")           private LocalDateTime deleted_at;
    @Column("usuario_creacion")     private Long usuario_creacion;
    @Column("usuario_modificacion") private Long usuario_modificacion;
}
