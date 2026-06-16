package com.cloud_tecnoligical.nyxora_erp.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Getter;
import lombok.Setter;

/** CPC — Clasificador de Productos y servicios presupuestal (R2DBC, jerárquico). */
@Table("cpc")
@Getter
@Setter
public class CpcEntity {

    @Id
    private Long id;

    @Column("empresa_id")        private Long empresa_id;
    @Column("vigencia_id")       private Long vigencia_id;
    @Column("cpc_padre_id")      private Long cpc_padre_id;
    @Column("codigo")            private String codigo;
    @Column("nombre")            private String nombre;
    @Column("maneja_movimiento") private Boolean maneja_movimiento;
    @Column("activo")            private Boolean activo;
    @Column("created_at")        private LocalDateTime created_at;
    @Column("updated_at")        private LocalDateTime updated_at;
    @Column("deleted_at")        private LocalDateTime deleted_at;
}
