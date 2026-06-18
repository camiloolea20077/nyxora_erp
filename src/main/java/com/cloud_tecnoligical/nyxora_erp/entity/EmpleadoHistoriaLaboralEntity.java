package com.cloud_tecnoligical.nyxora_erp.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Getter;
import lombok.Setter;

/** Historia laboral previa del empleado (R2DBC). */
@Table("empleado_historia_laboral")
@Getter
@Setter
public class EmpleadoHistoriaLaboralEntity {

    @Id
    private Long id;

    @Column("empresa_id")     private Long empresa_id;
    @Column("empleado_id")    private Long empleado_id;
    @Column("nombre_empresa") private String nombre_empresa;
    @Column("cargo")          private String cargo;
    @Column("tipo_contrato")  private String tipo_contrato;
    @Column("fecha_inicio")   private LocalDate fecha_inicio;
    @Column("fecha_final")    private LocalDate fecha_final;
    @Column("jefe_inmediato") private String jefe_inmediato;
    @Column("municipio_id")   private Long municipio_id;
    @Column("es_publico")     private Boolean es_publico;
    @Column("created_at")     private LocalDateTime created_at;
    @Column("updated_at")     private LocalDateTime updated_at;
    @Column("deleted_at")     private LocalDateTime deleted_at;
}
