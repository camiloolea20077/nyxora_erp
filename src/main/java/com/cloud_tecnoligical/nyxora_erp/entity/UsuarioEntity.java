package com.cloud_tecnoligical.nyxora_erp.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Getter;
import lombok.Setter;

/**
 * Entidad R2DBC (Spring Data Relational, NO JPA). Tabla 'usuario' (español snake_case).
 */
@Table("usuario")
@Getter
@Setter
public class UsuarioEntity {

    @Id
    private Long id;

    @Column("empresa_id")
    private Long empresa_id;

    @Column("username")
    private String username;

    @Column("email")
    private String email;

    @Column("hash_password")
    private String hash_password;

    @Column("activo")
    private Boolean activo;

    @Column("created_at")
    private LocalDateTime created_at;

    @Column("updated_at")
    private LocalDateTime updated_at;

    @Column("deleted_at")
    private LocalDateTime deleted_at;

    @Column("usuario_creacion")
    private Long usuario_creacion;

    @Column("usuario_modificacion")
    private Long usuario_modificacion;
}
