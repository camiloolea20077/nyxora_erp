package com.cloud_tecnoligical.nyxora_erp.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Getter;
import lombok.Setter;

/** Plantilla de cláusula contractual (R2DBC). */
@Table("clausula_plantilla")
@Getter
@Setter
public class ClausulaPlantillaEntity {

    @Id
    private Long id;

    @Column("empresa_id")    private Long empresa_id;
    @Column("tipo_clausula") private String tipo_clausula;
    @Column("numero")        private String numero;
    @Column("orden")         private String orden;
    @Column("nombre")        private String nombre;
    @Column("texto")         private String texto;
    @Column("activo")        private Boolean activo;
    @Column("created_at")    private LocalDateTime created_at;
    @Column("updated_at")    private LocalDateTime updated_at;
    @Column("deleted_at")    private LocalDateTime deleted_at;
}
