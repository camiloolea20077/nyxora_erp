package com.cloud_tecnoligical.nyxora_erp.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Getter;
import lombok.Setter;

/** Contrato de adquisición (R2DBC). */
@Table("contrato")
@Getter
@Setter
public class ContratoEntity {

    @Id
    private Long id;

    @Column("empresa_id")           private Long empresa_id;
    @Column("numero")               private String numero;
    @Column("nombre")               private String nombre;
    @Column("tipo_contrato")        private String tipo_contrato;
    @Column("contratista_id")       private Long contratista_id;
    @Column("modalidad_id")         private Long modalidad_id;
    @Column("objeto")               private String objeto;
    @Column("fecha_inicio")         private LocalDate fecha_inicio;
    @Column("fecha_fin")            private LocalDate fecha_fin;
    @Column("valor")                private BigDecimal valor;
    @Column("estado")               private String estado;
    @Column("activo")               private Boolean activo;
    @Column("created_at")           private LocalDateTime created_at;
    @Column("updated_at")           private LocalDateTime updated_at;
    @Column("deleted_at")           private LocalDateTime deleted_at;
    @Column("usuario_creacion")     private Long usuario_creacion;
    @Column("usuario_modificacion") private Long usuario_modificacion;
}
