package com.cloud_tecnoligical.nyxora_erp.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Getter;
import lombok.Setter;

/** Novedad de nómina (R2DBC), incluye embargos. */
@Table("novedad_nomina")
@Getter
@Setter
public class NovedadNominaEntity {

    @Id
    private Long id;

    @Column("empresa_id")             private Long empresa_id;
    @Column("vinculacion_id")         private Long vinculacion_id;
    @Column("concepto_nomina_id")     private Long concepto_nomina_id;
    @Column("tercero_id")             private Long tercero_id;
    @Column("descripcion")            private String descripcion;
    @Column("cantidad_valor")         private BigDecimal cantidad_valor;
    @Column("fecha_inicial")          private LocalDate fecha_inicial;
    @Column("fecha_final")            private LocalDate fecha_final;
    @Column("fecha_aplicada")         private LocalDate fecha_aplicada;
    @Column("numero_cuota")           private BigDecimal numero_cuota;
    @Column("dias")                   private Integer dias;
    @Column("tipo_ausentismo")        private String tipo_ausentismo;
    @Column("tipo_embargo")           private String tipo_embargo;
    @Column("expediente")             private String expediente;
    @Column("demandante")             private String demandante;
    @Column("banco_id")               private Long banco_id;
    @Column("numero_cuenta_bancaria") private String numero_cuenta_bancaria;
    @Column("estado_novedad")         private String estado_novedad;
    @Column("anulado")                private Boolean anulado;
    @Column("activo")                 private Boolean activo;
    @Column("created_at")             private LocalDateTime created_at;
    @Column("updated_at")             private LocalDateTime updated_at;
    @Column("deleted_at")             private LocalDateTime deleted_at;
    @Column("usuario_creacion")       private Long usuario_creacion;
    @Column("usuario_modificacion")   private Long usuario_modificacion;
}
