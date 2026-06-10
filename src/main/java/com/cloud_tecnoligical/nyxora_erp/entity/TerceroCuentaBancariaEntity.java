package com.cloud_tecnoligical.nyxora_erp.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Getter;
import lombok.Setter;

@Table("tercero_cuenta_bancaria")
@Getter
@Setter
public class TerceroCuentaBancariaEntity {

    @Id
    private Long id;

    @Column("tercero_id")              private Long tercero_id;
    @Column("banco_id")                private Long banco_id;
    @Column("tipo_cuenta_bancaria_id") private Long tipo_cuenta_bancaria_id;
    @Column("numero_cuenta")           private String numero_cuenta;
    @Column("principal")               private Boolean principal;
    @Column("activo")                  private Boolean activo;
    @Column("created_at")              private LocalDateTime created_at;
    @Column("updated_at")              private LocalDateTime updated_at;
    @Column("deleted_at")              private LocalDateTime deleted_at;
}
