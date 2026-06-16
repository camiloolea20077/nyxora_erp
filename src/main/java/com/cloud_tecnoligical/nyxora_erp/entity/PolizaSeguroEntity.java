package com.cloud_tecnoligical.nyxora_erp.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Getter;
import lombok.Setter;

/** Póliza de seguro (maestro reutilizado por activos fijos y contratación). R2DBC. */
@Table("poliza_seguro")
@Getter
@Setter
public class PolizaSeguroEntity {

    @Id
    private Long id;

    @Column("empresa_id")      private Long empresa_id;
    @Column("numero")          private String numero;
    @Column("aseguradora_id")  private Long aseguradora_id;
    @Column("tipo")            private String tipo;
    @Column("fecha_inicio")    private LocalDate fecha_inicio;
    @Column("fecha_fin")       private LocalDate fecha_fin;
    @Column("valor_asegurado") private BigDecimal valor_asegurado;
    @Column("activo")          private Boolean activo;
    @Column("created_at")      private LocalDateTime created_at;
    @Column("updated_at")      private LocalDateTime updated_at;
    @Column("deleted_at")      private LocalDateTime deleted_at;
}
