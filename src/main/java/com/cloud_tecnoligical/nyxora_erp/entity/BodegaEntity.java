package com.cloud_tecnoligical.nyxora_erp.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Getter;
import lombok.Setter;

/** Bodega (R2DBC, ← inv_bodegas). */
@Table("bodega")
@Getter
@Setter
public class BodegaEntity {

    @Id
    private Long id;

    @Column("empresa_id")           private Long empresa_id;
    @Column("sede_id")              private Long sede_id;
    @Column("centro_costo_id")      private Long centro_costo_id;
    @Column("codigo")               private String codigo;
    @Column("nombre")               private String nombre;
    @Column("tipo_abastecimiento")  private String tipo_abastecimiento;
    @Column("direccion")            private String direccion;
    @Column("latitud")              private String latitud;
    @Column("longitud")             private String longitud;
    @Column("permite_compra")       private Boolean permite_compra;
    @Column("activo")               private Boolean activo;
    @Column("created_at")           private LocalDateTime created_at;
    @Column("updated_at")           private LocalDateTime updated_at;
    @Column("deleted_at")           private LocalDateTime deleted_at;
    @Column("usuario_creacion")     private Long usuario_creacion;
    @Column("usuario_modificacion") private Long usuario_modificacion;
}
