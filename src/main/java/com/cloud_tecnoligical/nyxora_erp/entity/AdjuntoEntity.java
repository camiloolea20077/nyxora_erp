package com.cloud_tecnoligical.nyxora_erp.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Getter;
import lombok.Setter;

/** Adjunto polimórfico (R2DBC, ← com_adjuntos). Referencia por (modulo, entidad, entidad_id). */
@Table("adjunto")
@Getter
@Setter
public class AdjuntoEntity {

    @Id
    private Long id;

    @Column("empresa_id")       private Long empresa_id;
    @Column("modulo")           private String modulo;
    @Column("entidad")          private String entidad;
    @Column("entidad_id")       private Long entidad_id;
    @Column("nombre")           private String nombre;
    @Column("tipo_mime")        private String tipo_mime;
    @Column("url")              private String url;
    @Column("tamano_bytes")     private Long tamano_bytes;
    @Column("descripcion")      private String descripcion;
    @Column("activo")           private Boolean activo;
    @Column("created_at")       private LocalDateTime created_at;
    @Column("updated_at")       private LocalDateTime updated_at;
    @Column("deleted_at")       private LocalDateTime deleted_at;
    @Column("usuario_creacion") private Long usuario_creacion;
}
