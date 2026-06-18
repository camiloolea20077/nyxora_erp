package com.cloud_tecnoligical.nyxora_erp.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Getter;
import lombok.Setter;

/** Concepto de nómina (R2DBC). */
@Table("concepto_nomina")
@Getter
@Setter
public class ConceptoNominaEntity {

    @Id
    private Long id;

    @Column("empresa_id")               private Long empresa_id;
    @Column("codigo")                   private String codigo;
    @Column("nombre")                   private String nombre;
    @Column("frecuencia")               private String frecuencia;
    @Column("clase")                    private String clase;
    @Column("formula")                  private String formula;
    @Column("cuenta_credito_id")        private Long cuenta_credito_id;
    @Column("cuenta_patrono_id")        private Long cuenta_patrono_id;
    @Column("rubro_presupuestal_id")    private Long rubro_presupuestal_id;
    @Column("fuente_financiamiento_id") private Long fuente_financiamiento_id;
    @Column("tercero_id")               private Long tercero_id;
    @Column("activo")                   private Boolean activo;
    @Column("created_at")               private LocalDateTime created_at;
    @Column("updated_at")               private LocalDateTime updated_at;
    @Column("deleted_at")               private LocalDateTime deleted_at;
}
