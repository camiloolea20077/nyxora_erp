package com.cloud_tecnoligical.nyxora_erp.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Getter;
import lombok.Setter;

@Table("tercero_direccion")
@Getter
@Setter
public class TerceroDireccionEntity {

    @Id
    private Long id;

    @Column("tercero_id")    private Long tercero_id;
    @Column("tipo")          private String tipo;
    @Column("direccion")     private String direccion;
    @Column("municipio_id")  private Long municipio_id;
    @Column("barrio_id")     private Long barrio_id;
    @Column("codigo_postal") private String codigo_postal;
    @Column("telefono")      private String telefono;
    @Column("principal")     private Boolean principal;
    @Column("activo")        private Boolean activo;
    @Column("created_at")    private LocalDateTime created_at;
    @Column("updated_at")    private LocalDateTime updated_at;
    @Column("deleted_at")    private LocalDateTime deleted_at;
}
