package com.cloud_tecnoligical.nyxora_erp.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Getter;
import lombok.Setter;

/** Tenant raíz. NO lleva empresa_id (es la empresa). */
@Table("empresa")
@Getter
@Setter
public class EmpresaEntity {

    @Id
    private Long id;

    @Column("nit")
    private String nit;

    @Column("digito_verificacion")
    private Short digito_verificacion;

    @Column("razon_social")
    private String razon_social;

    @Column("nombre_comercial")
    private String nombre_comercial;

    @Column("codigo")
    private String codigo;

    @Column("tipo_persona")
    private String tipo_persona;

    @Column("representante_legal")
    private String representante_legal;

    @Column("regimen_tributario")
    private String regimen_tributario;

    @Column("tipo_contribuyente_id")
    private Long tipo_contribuyente_id;

    @Column("responsabilidad_fiscal")
    private String responsabilidad_fiscal;

    @Column("actividad_economica_id")
    private Long actividad_economica_id;

    @Column("sector")
    private String sector;

    @Column("email")
    private String email;

    @Column("telefono")
    private String telefono;

    @Column("celular")
    private String celular;

    @Column("sitio_web")
    private String sitio_web;

    @Column("municipio_id")
    private Long municipio_id;

    @Column("direccion")
    private String direccion;

    @Column("codigo_postal")
    private String codigo_postal;

    @Column("logo_url")
    private String logo_url;

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
