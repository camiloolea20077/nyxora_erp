package com.cloud_tecnoligical.nyxora_erp.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Getter;
import lombok.Setter;

/** Cargo (R2DBC). */
@Table("cargo")
@Getter
@Setter
public class CargoEntity {

    @Id
    private Long id;

    @Column("empresa_id")           private Long empresa_id;
    @Column("codigo")               private String codigo;
    @Column("nombre")               private String nombre;
    @Column("nivel_cargo")          private String nivel_cargo;
    @Column("grado")                private String grado;
    @Column("tipo_remuneracion")    private String tipo_remuneracion;
    @Column("sueldo_basico")        private BigDecimal sueldo_basico;
    @Column("sueldo_maximo")        private BigDecimal sueldo_maximo;
    @Column("mision")               private String mision;
    @Column("descripcion")          private String descripcion;
    @Column("activo")               private Boolean activo;
    @Column("created_at")           private LocalDateTime created_at;
    @Column("updated_at")           private LocalDateTime updated_at;
    @Column("deleted_at")           private LocalDateTime deleted_at;
    @Column("usuario_creacion")     private Long usuario_creacion;
    @Column("usuario_modificacion") private Long usuario_modificacion;
}
