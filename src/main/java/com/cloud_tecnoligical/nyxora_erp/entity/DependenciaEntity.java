package com.cloud_tecnoligical.nyxora_erp.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Getter;
import lombok.Setter;

/** Dependencia (R2DBC, ← com_dependencias). Jerárquica (nested set), bajo un centro de costo. */
@Table("dependencia")
@Getter
@Setter
public class DependenciaEntity {

    @Id
    private Long id;

    @Column("empresa_id")           private Long empresa_id;
    @Column("centro_costo_id")      private Long centro_costo_id;
    @Column("dependencia_padre_id") private Long dependencia_padre_id;
    @Column("codigo")               private String codigo;
    @Column("nombre")               private String nombre;
    @Column("ubicacion")            private String ubicacion;
    @Column("latitud")              private String latitud;
    @Column("longitud")             private String longitud;
    @Column("izquierda")            private Integer izquierda;
    @Column("derecha")              private Integer derecha;
    @Column("nivel")                private Integer nivel;
    @Column("activo")               private Boolean activo;
    @Column("created_at")           private LocalDateTime created_at;
    @Column("updated_at")           private LocalDateTime updated_at;
    @Column("deleted_at")           private LocalDateTime deleted_at;
    @Column("usuario_creacion")     private Long usuario_creacion;
    @Column("usuario_modificacion") private Long usuario_modificacion;
}
