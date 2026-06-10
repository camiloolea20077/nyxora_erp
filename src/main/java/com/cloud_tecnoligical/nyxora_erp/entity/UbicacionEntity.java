package com.cloud_tecnoligical.nyxora_erp.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Getter;
import lombok.Setter;

/** Ubicación dentro de una bodega (R2DBC, ← inv_ubicaciones). Jerárquica (nested set). */
@Table("ubicacion")
@Getter
@Setter
public class UbicacionEntity {

    @Id
    private Long id;

    @Column("empresa_id")         private Long empresa_id;
    @Column("bodega_id")          private Long bodega_id;
    @Column("ubicacion_padre_id") private Long ubicacion_padre_id;
    @Column("codigo")             private String codigo;
    @Column("nombre")             private String nombre;
    @Column("pasillo")            private Integer pasillo;
    @Column("altura")             private Integer altura;
    @Column("posicion")           private Integer posicion;
    @Column("izquierda")          private Integer izquierda;
    @Column("derecha")            private Integer derecha;
    @Column("nivel")              private Integer nivel;
    @Column("activo")             private Boolean activo;
    @Column("created_at")         private LocalDateTime created_at;
    @Column("updated_at")         private LocalDateTime updated_at;
    @Column("deleted_at")         private LocalDateTime deleted_at;
}
