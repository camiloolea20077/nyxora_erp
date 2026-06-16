package com.cloud_tecnoligical.nyxora_erp.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Getter;
import lombok.Setter;

/** Cláusula de un contrato (R2DBC). */
@Table("contrato_clausula")
@Getter
@Setter
public class ContratoClausulaEntity {

    @Id
    private Long id;

    @Column("contrato_id") private Long contrato_id;
    @Column("numero")      private String numero;
    @Column("orden")       private String orden;
    @Column("nombre")      private String nombre;
    @Column("texto")       private String texto;
    @Column("created_at")  private LocalDateTime created_at;
    @Column("deleted_at")  private LocalDateTime deleted_at;
}
