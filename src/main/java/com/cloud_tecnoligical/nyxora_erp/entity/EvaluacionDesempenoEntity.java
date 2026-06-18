package com.cloud_tecnoligical.nyxora_erp.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Getter;
import lombok.Setter;

/** Evaluación de desempeño de un empleado (R2DBC). */
@Table("evaluacion_desempeno")
@Getter
@Setter
public class EvaluacionDesempenoEntity {

    @Id
    private Long id;

    @Column("empresa_id")             private Long empresa_id;
    @Column("evaluacion_programa_id") private Long evaluacion_programa_id;
    @Column("empleado_id")            private Long empleado_id;
    @Column("tipo_evaluacion")        private String tipo_evaluacion;
    @Column("fecha_inicial")          private LocalDateTime fecha_inicial;
    @Column("fecha_final")            private LocalDateTime fecha_final;
    @Column("calificacion")           private BigDecimal calificacion;
    @Column("created_at")             private LocalDateTime created_at;
    @Column("updated_at")             private LocalDateTime updated_at;
    @Column("deleted_at")             private LocalDateTime deleted_at;
}
