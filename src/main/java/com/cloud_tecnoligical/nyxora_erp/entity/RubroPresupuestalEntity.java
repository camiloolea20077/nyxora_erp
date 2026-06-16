package com.cloud_tecnoligical.nyxora_erp.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Getter;
import lombok.Setter;

/** Rubro presupuestal — definición (R2DBC, jerárquico). tipo_rubro: ingreso | gasto. */
@Table("rubro_presupuestal")
@Getter
@Setter
public class RubroPresupuestalEntity {

    @Id
    private Long id;

    @Column("empresa_id")                  private Long empresa_id;
    @Column("vigencia_id")                 private Long vigencia_id;
    @Column("rubro_padre_id")              private Long rubro_padre_id;
    @Column("tipo_rubro")                  private String tipo_rubro;
    @Column("codigo_rubro")                private String codigo_rubro;
    @Column("nombre_rubro")                private String nombre_rubro;
    @Column("maneja_movimiento")           private Boolean maneja_movimiento;
    @Column("homologacion_circular_unica") private String homologacion_circular_unica;
    @Column("izquierda")                   private Integer izquierda;
    @Column("derecha")                     private Integer derecha;
    @Column("nivel")                       private Integer nivel;
    @Column("activo")                      private Boolean activo;
    @Column("created_at")                  private LocalDateTime created_at;
    @Column("updated_at")                  private LocalDateTime updated_at;
    @Column("deleted_at")                  private LocalDateTime deleted_at;
    @Column("usuario_creacion")            private Long usuario_creacion;
    @Column("usuario_modificacion")        private Long usuario_modificacion;
}
