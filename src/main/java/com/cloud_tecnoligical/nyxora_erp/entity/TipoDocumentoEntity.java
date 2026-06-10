package com.cloud_tecnoligical.nyxora_erp.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Getter;
import lombok.Setter;

/** Tipo de documento (R2DBC). Define la numeración transaccional por módulo. */
@Table("tipo_documento")
@Getter
@Setter
public class TipoDocumentoEntity {

    @Id
    private Long id;

    @Column("empresa_id")            private Long empresa_id;
    @Column("modulo")                private String modulo;
    @Column("codigo")                private String codigo;
    @Column("nombre")                private String nombre;
    @Column("prefijo")               private String prefijo;
    @Column("reinicia_por_vigencia") private Boolean reinicia_por_vigencia;
    @Column("activo")                private Boolean activo;
    @Column("created_at")            private LocalDateTime created_at;
    @Column("updated_at")            private LocalDateTime updated_at;
    @Column("deleted_at")            private LocalDateTime deleted_at;
    @Column("usuario_creacion")      private Long usuario_creacion;
    @Column("usuario_modificacion")  private Long usuario_modificacion;
}
