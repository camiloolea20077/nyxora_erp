package com.cloud_tecnoligical.nyxora_erp.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Getter;
import lombok.Setter;

@Table("tercero_contacto")
@Getter
@Setter
public class TerceroContactoEntity {

    @Id
    private Long id;

    @Column("tercero_id") private Long tercero_id;
    @Column("nombre")     private String nombre;
    @Column("cargo")      private String cargo;
    @Column("telefono")   private String telefono;
    @Column("celular")    private String celular;
    @Column("email")      private String email;
    @Column("notas")      private String notas;
    @Column("principal")  private Boolean principal;
    @Column("activo")     private Boolean activo;
    @Column("created_at") private LocalDateTime created_at;
    @Column("updated_at") private LocalDateTime updated_at;
    @Column("deleted_at") private LocalDateTime deleted_at;
}
