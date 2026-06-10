package com.cloud_tecnoligical.nyxora_erp.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Getter;
import lombok.Setter;

/** Centro de costo (R2DBC, ← com_centros_costos). Jerárquico (nested set). */
@Table("centro_costo")
@Getter
@Setter
public class CentroCostoEntity {

    @Id
    private Long id;

    @Column("empresa_id")             private Long empresa_id;
    @Column("sede_id")                private Long sede_id;
    @Column("centro_costo_padre_id")  private Long centro_costo_padre_id;
    @Column("codigo")                 private String codigo;
    @Column("nombre")                 private String nombre;
    @Column("tipo_centro_costo")      private String tipo_centro_costo;
    @Column("clase_centro_costo")     private String clase_centro_costo;
    @Column("es_observacion")         private Boolean es_observacion;
    @Column("maneja_plan_financiero") private Boolean maneja_plan_financiero;
    @Column("tercero_id")             private Long tercero_id;
    @Column("direccion")              private String direccion;
    @Column("unidad_negocio_id")      private Long unidad_negocio_id;
    @Column("izquierda")              private Integer izquierda;
    @Column("derecha")                private Integer derecha;
    @Column("nivel")                  private Integer nivel;
    @Column("activo")                 private Boolean activo;
    @Column("created_at")             private LocalDateTime created_at;
    @Column("updated_at")             private LocalDateTime updated_at;
    @Column("deleted_at")             private LocalDateTime deleted_at;
    @Column("usuario_creacion")       private Long usuario_creacion;
    @Column("usuario_modificacion")   private Long usuario_modificacion;
}
