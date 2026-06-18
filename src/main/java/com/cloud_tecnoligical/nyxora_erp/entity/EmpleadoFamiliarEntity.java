package com.cloud_tecnoligical.nyxora_erp.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Getter;
import lombok.Setter;

/** Familiar del empleado (R2DBC). */
@Table("empleado_familiar")
@Getter
@Setter
public class EmpleadoFamiliarEntity {

    @Id
    private Long id;

    @Column("empresa_id")            private Long empresa_id;
    @Column("empleado_id")           private Long empleado_id;
    @Column("nombre_apellido")       private String nombre_apellido;
    @Column("fecha_nacimiento")      private LocalDate fecha_nacimiento;
    @Column("parentesco")            private String parentesco;
    @Column("a_cargo")               private Boolean a_cargo;
    @Column("vivo")                  private Boolean vivo;
    @Column("convive")               private Boolean convive;
    @Column("dependiente_retencion") private Boolean dependiente_retencion;
    @Column("created_at")            private LocalDateTime created_at;
    @Column("updated_at")            private LocalDateTime updated_at;
    @Column("deleted_at")            private LocalDateTime deleted_at;
}
