package com.cloud_tecnoligical.nyxora_erp.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Getter;
import lombok.Setter;

/** Arqueo (cuadre) de una caja (R2DBC, append-only). */
@Table("arqueo")
@Getter
@Setter
public class ArqueoEntity {

    @Id
    private Long id;

    @Column("empresa_id")       private Long empresa_id;
    @Column("caja_id")          private Long caja_id;
    @Column("fecha")            private LocalDateTime fecha;
    @Column("valor_declarado")  private BigDecimal valor_declarado;
    @Column("valor_sistema")    private BigDecimal valor_sistema;
    @Column("diferencia")       private BigDecimal diferencia;
    @Column("observaciones")    private String observaciones;
    @Column("usuario_creacion") private Long usuario_creacion;
    @Column("created_at")       private LocalDateTime created_at;
}
